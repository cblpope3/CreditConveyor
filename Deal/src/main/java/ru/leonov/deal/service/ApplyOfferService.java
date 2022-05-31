package ru.leonov.deal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.leonov.deal.dto.LoanOfferDTO;
import ru.leonov.deal.exceptions.ApplyOfferException;
import ru.leonov.deal.model.entity.ApplicationEntity;
import ru.leonov.deal.repository.ApplicationRepository;

import static ru.leonov.deal.utility.ApplicationUtility.updateApplicationStatus;

/**
 * Service that works with application confirmations.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ApplyOfferService {

    private final ApplicationRepository applicationRepository;

    /**
     * Client applies offer.
     *
     * @param appliedOffer offer, that client had been applied.
     */
    public void applyOffer(LoanOfferDTO appliedOffer) {

        Long applicationId = appliedOffer.getApplicationId();

        ApplicationEntity appliedApplication = applicationRepository.findById(applicationId).orElse(null);

        if (appliedApplication == null) {
            log.warn("Can't find application with given id: {}.", appliedOffer.getApplicationId());
            throw new ApplyOfferException(ApplyOfferException.ExceptionCause.APPLICATION_NOT_FOUND);
        }

        //adding new status to status history
        updateApplicationStatus(appliedApplication, ApplicationEntity.Status.APPROVED);

        appliedApplication.setAppliedOffer(appliedOffer);

        applicationRepository.save(appliedApplication);
    }
}
