package ru.leonov.deal.exception

/**
 * Exception that is thrown during loan offer application process.
 */
data class ApplicationException(val exceptionCause: ExceptionCause) :
    RuntimeException(exceptionCause.userFriendlyMessage) {

    /**
     * Exception cause enum.
     */
    enum class ExceptionCause(val userFriendlyMessage: String) {
        APPLICATION_NOT_FOUND("Requested application not found in database.")
    }

}
