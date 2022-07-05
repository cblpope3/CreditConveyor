package ru.leonov.deal.model.record

import ru.leonov.deal.model.enums.EmploymentPositionEnum
import ru.leonov.deal.model.enums.EmploymentStatusEnum
import java.math.BigDecimal

/**
 * POJO that contain information about client's employment.
 */
data class EmploymentRecord(
    /**
     * Client's employment status.
     *
     * @see EmploymentStatusEnum
     */
    var employmentStatus: EmploymentStatusEnum,
    /**
     * Client's employer organization name.
     */
    //todo check if employerInn is changed to employer everywhere
    val employer: String,
    /**
     * Current client's salary.
     */
    val salary: BigDecimal,
    /**
     * Clients job position.
     *
     * @see EmploymentPositionEnum
     */
    val position: EmploymentPositionEnum,
    /**
     * Client's total work experience in months.
     */
    val workExperienceTotal: Int,
    /**
     * Client's work experience in current position in months.
     */
    val workExperienceCurrent: Int
)
