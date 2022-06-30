package ru.leonov.deal.controller

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.leonov.deal.dto.FinishRegistrationRequestDTO
import ru.leonov.deal.dto.LoanApplicationRequestDTO
import ru.leonov.deal.dto.LoanOfferDTO
import ru.leonov.deal.exception.ApplicationException
import ru.leonov.deal.service.ApplyOfferService
import ru.leonov.deal.service.CreditCalculationService
import ru.leonov.deal.service.GetOffersService
import java.util.*
import java.util.stream.Collectors

/**
 * Controller that handle '/deal' rest requests.
 */
@RestController
class DealController(
    @Autowired val getOffersService: GetOffersService,
    @Autowired val applyOfferService: ApplyOfferService,
    @Autowired val creditCalculationService: CreditCalculationService
) : DealApi {

    private val log = KotlinLogging.logger {}

    /**
     * Request of loan offer list.
     *
     * @param loanApplicationRequestDTO pre-scoring data of request.
     * @return {@link List} of generated loan offers. Sorted from worst to best.
     */
    override fun postDealApplication(
        loanApplicationRequestDTO: LoanApplicationRequestDTO
    ): ResponseEntity<List<LoanOfferDTO>> {
        log.debug { "Got loan offers request: $loanApplicationRequestDTO" }

        val resultList = getOffersService.getOffers(loanApplicationRequestDTO)
        log.trace { "Returning response: $resultList" }

        return ResponseEntity(resultList, HttpStatus.OK)
    }

    /**
     * Apply one of previously received loan offers.
     *
     * @param loanOfferDTO loan offer that is have to be applied.
     * @return Code 200 if loan offer applied successfully.
     */
    override fun putDealOffer(loanOfferDTO: LoanOfferDTO): ResponseEntity<Unit> {
        log.debug { "Got request to apply offer #${loanOfferDTO.applicationId}" }

        applyOfferService.applyOffer(loanOfferDTO)

        log.debug { "Offer application request #${loanOfferDTO.applicationId} processed successfully." }
        return ResponseEntity(HttpStatus.OK)
    }

    /**
     * Finish loan application process.
     *
     * @param applicationId                id of loan application.
     * @param finishRegistrationRequestDTO data to finish registration.
     * @return code 200 if registration is finished successfully.
     */
    override fun putDealCalculate(
        applicationId: Long,
        finishRegistrationRequestDTO: FinishRegistrationRequestDTO
    ): ResponseEntity<Unit> {
        log.debug { "Got calculate credit offer #$applicationId request." }

        creditCalculationService.calculateCredit(finishRegistrationRequestDTO, applicationId)

        log.debug { "Calculate credit offer #$applicationId request calculated successfully." }
        return ResponseEntity(HttpStatus.OK)
    }

    /**
     * Method handle custom [ApplicationException] that is thrown during request processing.
     *
     * @param e exception that happened during request processing.
     * @return [ErrorResponseJSON] with problem explanation.
     */
    @Suppress("unused")
    @ExceptionHandler(ApplicationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun applicationExceptionHandler(e: ApplicationException): ResponseEntity<ErrorResponseJSON> {
        val responseMessage = e.message
        return if (e.exceptionCause == ApplicationException.ExceptionCause.APPLICATION_NOT_FOUND) {
            log.debug { "Not found application with requested id." }

            ResponseEntity(ErrorResponseJSON(problemMessage = responseMessage), HttpStatus.NOT_FOUND)
        } else {
            log.debug { "Got unexpected exception: $responseMessage." }

            ResponseEntity(ErrorResponseJSON(problemMessage = responseMessage), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Method that performs request parameter validation.
     *
     * @param e [MethodArgumentNotValidException] that was thrown during validation.
     * @return [List] of [ErrorResponseJSON] with happened issues explanation.
     */
    @Suppress("unused")
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun validationError(e: MethodArgumentNotValidException): ResponseEntity<List<ErrorResponseJSON>> {
        val errorResponseContainerList = e.bindingResult.fieldErrors.stream()
            .map { fieldError: FieldError ->
                ErrorResponseJSON(
                    problemFieldName = fieldError.field,
                    rejectedValue = Objects.toString(fieldError.rejectedValue, "Not defined."),
                    problemMessage = fieldError.defaultMessage
                )
            }
            .collect(Collectors.toList())

        log.debug { "Got bad request: $errorResponseContainerList" }
        return ResponseEntity(errorResponseContainerList, HttpStatus.BAD_REQUEST)
    }
}
