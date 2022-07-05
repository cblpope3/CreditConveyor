package ru.leonov.deal.service

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.leonov.deal.client.ConveyorAppClient
import ru.leonov.deal.dto.FinishRegistrationRequestDTO
import ru.leonov.deal.exception.ApplicationException
import ru.leonov.deal.mapper.mapApplicationEntityToScoringDataDTO
import ru.leonov.deal.mapper.mapCreditDTOtoCreditEntity
import ru.leonov.deal.mapper.mapFinishRegistrationToClient
import ru.leonov.deal.model.entity.ApplicationEntity
import ru.leonov.deal.model.enums.ApplicationStatusEnum
import ru.leonov.deal.model.enums.CreditStatusEnum
import ru.leonov.deal.repository.ApplicationRepository
import ru.leonov.deal.repository.ClientRepository
import ru.leonov.deal.repository.CreditRepository
import ru.leonov.deal.utility.updateApplicationStatus

/**
 * Service that handle end of registration.
 */
@Service
class CreditCalculationService(
    @Autowired val applicationRepository: ApplicationRepository,
    @Autowired val creditRepository: CreditRepository,
    @Autowired val clientRepository: ClientRepository,
    @Autowired val conveyorAppClient: ConveyorAppClient
) {

    private val log = KotlinLogging.logger {}

    /**
     * Request credit calculation from Conveyor-App, save new credit parameters to database.
     *
     * @param registrationRequestDTO data to finish registration.
     * @param applicationId          loan application id.
     */
    fun calculateCredit(registrationRequestDTO: FinishRegistrationRequestDTO, applicationId: Long) {

        // 1. По API приходит объект FinishRegistrationRequestDTO и параметр applicationId
        // (Long).
        // 2. Достаётся из БД заявка(Application) по applicationId.
        val application: ApplicationEntity = applicationRepository.findById(applicationId).orElse(null)
            ?: let {
                log.warn { "Can't calculate credit: requested application #$applicationId not found in database." }
                throw ApplicationException(ApplicationException.ExceptionCause.APPLICATION_NOT_FOUND)
            }
        //todo watch bp diagram
        if (application.status == ApplicationStatusEnum.CC_DENIED ||
            application.status == ApplicationStatusEnum.CLIENT_DENIED
        ) {
            log.warn { "Can't calculate credit: requested application #$applicationId is archived." }
            return
        }

        // updating client information with data from registration request.
        var client = mapFinishRegistrationToClient(
            registrationRequestDTO, application.client
        )
        client = clientRepository.save(client)
        application.client = client

        // 3. ScoringDataDTO насыщается дополнительной информацией из
        // FinishRegistrationRequestDTO и Client, который хранится в Application
        val scoringDataDTO = mapApplicationEntityToScoringDataDTO(application)

        // 4. Отправляется POST запрос на /conveyor/calculation МС conveyor с телом
        // ScoringDataDTO через FeignClient.
        val calculatedCredit = conveyorAppClient.requestCreditCalculation(scoringDataDTO)
        if (calculatedCredit == null) {
            //assuming that if credit was denied by conveyor, response status will be 204 (No content).
            updateApplicationStatus(application, ApplicationStatusEnum.CC_DENIED)
            log.debug { "Requested application is denied." }
        } else {
            // 5. На основе полученного из кредитного конвейера CreditDTO создаётся
            // сущность Credit и сохраняется в базу со статусом CALCULATED.
            var creditEntity = mapCreditDTOtoCreditEntity(calculatedCredit)
            creditEntity.creditStatus = CreditStatusEnum.CALCULATED
            creditEntity = creditRepository.save(creditEntity)

            // 6. В заявке обновляется статус, история статусов.
            application.credit = creditEntity
            updateApplicationStatus(application, ApplicationStatusEnum.CC_APPROVED)
        }

        // 7. Заявка сохраняется
        applicationRepository.save(application)
    }
}
