package ru.leonov.deal.model.record

/**
 * POJO that contain information about additional services applied to client.
 */
data class AdditionalServicesRecord(
    /**
     * Did client purchase insurance?
     */
    val isInsuranceEnabled: Boolean,
    /**
     * Is this client salary client?
     */
    val isSalaryClient: Boolean
)
