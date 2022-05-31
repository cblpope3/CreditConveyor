package ru.leonov.deal.utility;

import ru.leonov.deal.model.entity.ApplicationEntity;
import ru.leonov.deal.model.entity.ClientEntity;
import ru.leonov.deal.model.record.ApplicationHistoryElementRecord;

import java.time.LocalDate;
import java.util.ArrayList;

import static ru.leonov.deal.model.entity.ApplicationEntity.Status.PREAPPROVAL;

/**
 * Utility class that maintain {@link ApplicationEntity} needs.
 */
public class ApplicationUtility {

    private ApplicationUtility() {
        throw new IllegalStateException("Application service is utility class.");
    }

    /**
     * Update current status to {@link ApplicationEntity}. Also, append new status history item to application status
     * history.
     *
     * @param application application that needs to update status.
     * @param newStatus   new status of given application.
     */
    public static void updateApplicationStatus(ApplicationEntity application, ApplicationEntity.Status newStatus) {

        //updating current application status
        application.setStatus(newStatus);

        //adding new status to status history
        ApplicationHistoryElementRecord newHistoryElement = new ApplicationHistoryElementRecord();
        newHistoryElement.setDate(LocalDate.now());
        newHistoryElement.setStatus(newStatus);

        if (application.getStatusHistory() == null) {
            application.setStatusHistory(new ArrayList<>());
        }
        application.getStatusHistory().add(newHistoryElement);
    }

    /**
     * Create new application for given client.
     *
     * @param client client that requested new application.
     * @return new {@link ApplicationEntity} instance with PREAPPROVAL status and correct status history.
     */
    public static ApplicationEntity createNewApplication(ClientEntity client) {
        ApplicationEntity application = new ApplicationEntity();
        application.setClient(client);
        updateApplicationStatus(application, PREAPPROVAL);
        application.setCreationDate(LocalDate.now());

        return application;
    }
}
