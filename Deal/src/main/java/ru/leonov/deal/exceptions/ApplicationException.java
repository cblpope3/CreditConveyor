package ru.leonov.deal.exceptions;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Exception that is thrown during loan offer application process.
 */
@EqualsAndHashCode(callSuper = false)
public class ApplicationException extends RuntimeException {

    @Getter
    private final ExceptionCause exceptionCause;

    public ApplicationException(ExceptionCause exceptionCause) {
        super(exceptionCause.getUserFriendlyMessage());
        this.exceptionCause = exceptionCause;
    }

    /**
     * Exception cause enum.
     */
    public enum ExceptionCause {

        APPLICATION_NOT_FOUND("Requested application not found in database.");

        @Getter
        private final String userFriendlyMessage;

        ExceptionCause(String userFriendlyMessage) {
            this.userFriendlyMessage = userFriendlyMessage;
        }

    }
}
