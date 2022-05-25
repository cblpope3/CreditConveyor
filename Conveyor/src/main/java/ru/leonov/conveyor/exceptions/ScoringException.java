package ru.leonov.conveyor.exceptions;

/**
 * Exception that can be thrown during scoring process.
 */
public class ScoringException extends Exception {

    public ScoringException(ExceptionCause exceptionCause) {
        super(exceptionCause.getUserFriendlyMessage());
    }

    /**
     * Enum that specify exception cause.
     */
    public enum ExceptionCause {

        UNACCEPTABLE_EMPLOYER_STATUS("Clients job status is unacceptable."),
        INSUFFICIENT_SALARY("Clients salary is insufficient to get this credit."),
        UNACCEPTABLE_AGE("Client age is out of acceptable range."),
        INSUFFICIENT_EXPERIENCE("Client work experience is insufficient.");

        private final String userFriendlyMessage;

        ExceptionCause(String userFriendlyMessage) {
            this.userFriendlyMessage = userFriendlyMessage;
        }

        public String getUserFriendlyMessage() {
            return userFriendlyMessage;
        }
    }
}

