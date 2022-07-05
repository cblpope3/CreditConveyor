package ru.leonov.deal.model.record

import java.time.LocalDate

/**
 * POJO that contain information about client's passport.
 */
data class PassportRecord (
    /**
     * Client's passport series.
     */
    var series: Int,
    /**
    * Client's passport number.
    */
    var number: Int,
    /**
    * Client's passport issue date.
    */
    var issueDate: LocalDate? = null,
    /**
     * Name of client's passport issue organization.
     */
    var issueBranch: String? = null
)
