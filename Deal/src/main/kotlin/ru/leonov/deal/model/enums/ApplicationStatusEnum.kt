package ru.leonov.deal.model.enums

/**
 * Credit application status enum. Possible values are: 'PREAPPROVAL', 'APPROVED', 'CC_DENIED', 'CC_APPROVED',
 * 'PREPARE_DOCUMENTS', 'DOCUMENT_CREATED', 'CLIENT_DENIED', 'DOCUMENT_SIGNED', 'CREDIT_ISSUED',
 */
@Suppress("unused")
enum class ApplicationStatusEnum {
    /**
     * Application is pre-approved.
     */
    PREAPPROVAL,

    /**
     * Application is approved.
     */
    APPROVED,

    /**
     * Application is denied by credit-conveyor.
     */
    CC_DENIED,

    /**
     * Application is approved by credit-conveyor.
     */
    CC_APPROVED,

    /**
     * Documents for application are preparing.
     */
    PREPARE_DOCUMENTS,

    /**
     * Documents for application had been created.
     */
    DOCUMENT_CREATED,

    /**
     * Application is denied by client.
     */
    CLIENT_DENIED,

    /**
     * Documents for application are signed.
     */
    DOCUMENT_SIGNED,

    /**
     * Credit is issued.
     */
    CREDIT_ISSUED
}
