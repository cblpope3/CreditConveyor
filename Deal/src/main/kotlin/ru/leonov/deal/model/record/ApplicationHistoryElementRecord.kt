package ru.leonov.deal.model.record

import ru.leonov.deal.model.enums.ApplicationStatusEnum
import java.time.LocalDate

/**
 * POJO that represent element of credit application history.
 */
data class ApplicationHistoryElementRecord(
    /**
     * New status of credit application.
     */
    val status: ApplicationStatusEnum,
    /**
     * Date of status changing.
     */
    val date: LocalDate
)
