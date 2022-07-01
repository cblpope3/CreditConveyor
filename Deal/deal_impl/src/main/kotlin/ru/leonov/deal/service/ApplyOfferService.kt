package ru.leonov.deal.service

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.leonov.deal.dto.LoanOfferDTO
import ru.leonov.deal.exception.ApplicationException
import ru.leonov.deal.model.enums.ApplicationStatusEnum
import ru.leonov.deal.repository.ApplicationRepository
import ru.leonov.deal.utility.updateApplicationStatus

/**
 * Service that works with application confirmations.
 */
@Service
class ApplyOfferService(
    @Autowired val applicationRepository: ApplicationRepository
) {

    private val log = KotlinLogging.logger {}

    /**
     * Client applies offer.
     *
     * @param appliedOffer offer, that client had been applied.
     */
    fun applyOffer(appliedOffer: LoanOfferDTO) {
        val applicationId = appliedOffer.applicationId

        val appliedApplication = applicationRepository.findById(applicationId).orElse(null)
            ?: let {
                log.warn { "Can't find application with given id: ${appliedOffer.applicationId}." }
                throw ApplicationException(ApplicationException.ExceptionCause.APPLICATION_NOT_FOUND)
            }

        //adding new status to status history
        updateApplicationStatus(appliedApplication, ApplicationStatusEnum.APPROVED)
        appliedApplication.appliedOffer = appliedOffer
        applicationRepository.save(appliedApplication)
    }
}
