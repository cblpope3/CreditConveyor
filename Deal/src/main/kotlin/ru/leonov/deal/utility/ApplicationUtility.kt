package ru.leonov.deal.utility

import ru.leonov.deal.model.entity.ApplicationEntity
import ru.leonov.deal.model.entity.ClientEntity
import ru.leonov.deal.model.enums.ApplicationStatusEnum
import ru.leonov.deal.model.record.ApplicationHistoryElementRecord
import java.time.LocalDate

/**
 * Update current status to [ApplicationEntity]. Also, append new status history item to application status
 * history.
 *
 * @param application application that needs to update status.
 * @param newStatus   new status of given application.
 */
fun updateApplicationStatus(application: ApplicationEntity, newStatus: ApplicationStatusEnum) {

    //updating current application status
    application.status = newStatus

    //adding new status to status history
    val newHistoryElement = ApplicationHistoryElementRecord(newStatus, LocalDate.now())
    application.statusHistory.add(newHistoryElement)
}

/**
 * Create new application for given client.
 *
 * @param client client that requested new application.
 * @return new [ApplicationEntity] instance with PREAPPROVAL status and correct status history.
 */
fun createNewApplication(client: ClientEntity): ApplicationEntity {
    return ApplicationEntity(
        client = client,
        status = ApplicationStatusEnum.PREAPPROVAL,
        creationDate = LocalDate.now(),
        statusHistory = mutableListOf(getNewStatus(ApplicationStatusEnum.PREAPPROVAL))
    )
}

/**
 * Generate new [ApplicationHistoryElementRecord] with new status and current date.
 */
fun getNewStatus(newStatus: ApplicationStatusEnum): ApplicationHistoryElementRecord {
    return ApplicationHistoryElementRecord(newStatus, LocalDate.now())
}
