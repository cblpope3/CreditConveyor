package ru.leonov.deal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.leonov.deal.client.ConveyorAppClient;
import ru.leonov.deal.dto.CreditDTO;
import ru.leonov.deal.dto.FinishRegistrationRequestDTO;
import ru.leonov.deal.dto.ScoringDataDTO;
import ru.leonov.deal.exceptions.ApplicationException;
import ru.leonov.deal.mappers.FinishRegistrationMapper;
import ru.leonov.deal.model.entity.ApplicationEntity;
import ru.leonov.deal.model.entity.ClientEntity;
import ru.leonov.deal.model.entity.CreditEntity;
import ru.leonov.deal.repository.ApplicationRepository;
import ru.leonov.deal.repository.ClientRepository;
import ru.leonov.deal.repository.CreditRepository;
import ru.leonov.deal.utility.ApplicationUtility;

/**
 * Service that handle end of registration.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CreditCalculationService {

    private final ApplicationRepository applicationRepository;
    private final CreditRepository creditRepository;
    private final ClientRepository clientRepository;
    private final ConveyorAppClient conveyorAppClient;

    /**
     * Request credit calculation from Conveyor-App, save new credit parameters to database.
     *
     * @param registrationRequestDTO data to finish registration.
     * @param applicationId          loan application id.
     */
    public void calculateCredit(FinishRegistrationRequestDTO registrationRequestDTO, Long applicationId) {

        // 1. По API приходит объект FinishRegistrationRequestDTO и параметр applicationId
        // (Long).
        // 2. Достаётся из БД заявка(Application) по applicationId.
        ApplicationEntity application = applicationRepository.findById(applicationId).orElse(null);
        if (application == null) {
            log.warn("Can't calculate credit: requested application #{} not found in database.", applicationId);
            throw new ApplicationException(ApplicationException.ExceptionCause.APPLICATION_NOT_FOUND);
        } else if (application.getStatus() == ApplicationEntity.Status.CC_DENIED ||
                application.getStatus() == ApplicationEntity.Status.CLIENT_DENIED) {
            log.warn("Can't calculate credit: requested application #{} is archived.", applicationId);
            return;
        }

        // updating client information with data from registration request.
        ClientEntity client = FinishRegistrationMapper.INSTANCE.mapFinishRegistrationToClient(
                registrationRequestDTO, application.getClient());
        client = clientRepository.save(client);
        application.setClient(client);

        // 3. ScoringDataDTO насыщается дополнительной информацией из
        // FinishRegistrationRequestDTO и Client, который хранится в Application
        ScoringDataDTO scoringDataDTO = FinishRegistrationMapper.INSTANCE.mapApplicationEntityToScoringData(
                application);

        // 4. Отправляется POST запрос на /conveyor/calculation МС conveyor с телом
        // ScoringDataDTO через FeignClient.
        CreditDTO calculatedCredit = conveyorAppClient.requestCreditCalculation(scoringDataDTO);

        if (calculatedCredit == null) {
            //assuming that if credit was denied by conveyor, response status will be 204 (No content).
            ApplicationUtility.updateApplicationStatus(application, ApplicationEntity.Status.CC_DENIED);
            log.debug("Requested application is denied.");
        } else {
            // 5. На основе полученного из кредитного конвейера CreditDTO создаётся
            // сущность Credit и сохраняется в базу со статусом CALCULATED.
            CreditEntity creditEntity = FinishRegistrationMapper.INSTANCE.mapCreditDTOtoCreditEntity(calculatedCredit);
            creditEntity.setCreditStatus(CreditEntity.CreditStatus.CALCULATED);
            creditEntity = creditRepository.save(creditEntity);

            // 6. В заявке обновляется статус, история статусов.
            application.setCredit(creditEntity);
            ApplicationUtility.updateApplicationStatus(application, ApplicationEntity.Status.CC_APPROVED);
        }

        // 7. Заявка сохраняется
        applicationRepository.save(application);

    }
}
