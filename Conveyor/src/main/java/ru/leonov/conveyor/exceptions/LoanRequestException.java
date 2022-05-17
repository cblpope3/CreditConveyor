package ru.leonov.conveyor.exceptions;

/**
 * Exception that can be thrown during loan request validation.
 */
public class LoanRequestException extends Exception {

    /**
     * Constructor with additional parameter.
     *
     * @param exceptionCause cause of exception.
     * @param parameter      current value of variable that caused exception.
     */
    public LoanRequestException(ExceptionCause exceptionCause, String parameter) {
        super(String.format("%s Actual is '%s'", exceptionCause.getUserFriendlyMessage(), parameter));
    }

    /**
     * Basic exception constructor.
     *
     * @param exceptionCause cause of exception.
     */
    public LoanRequestException(ExceptionCause exceptionCause) {
        super(exceptionCause.getUserFriendlyMessage());
    }

    /**
     * Enum that specify exception cause.
     */
    public enum ExceptionCause {
        /**
         * One of required request parameters is null.
         */
        EMPTY_REQUIRED_PARAMETER("One of required parameter in request is empty."),
        /**
         * Entered first, last or middle name is not correct.
         */
        INCORRECT_NAME("Wrong name format."),
        /**
         * Credit amount is not correct.
         */
        INCORRECT_CREDIT_AMOUNT("Credit amount is incorrect. Must be real number > 10000."),
        /**
         * Credit term is not correct
         */
        INCORRECT_CREDIT_TERM("Credit term is incorrect. Must be integer > 6."),
        /**
         * Person that request credit is too young.
         */
        PERSON_TOO_YOUNG("Person's age is less than 18 years."),
        /**
         * Email has not valid format.
         */
        INCORRECT_EMAIL("Wrong email format."),
        /**
         * Passport series is not 4 digits long.
         */
        INCORRECT_PASSPORT_SERIES("Wrong passport series format, must be 4 digits."),
        /**
         * Passport number is not 6 digits long.
         */
        INCORRECT_PASSPORT_NUMBER("Wrong passport number format, must be 6 digits.");

        private final String userFriendlyMessage;

        ExceptionCause(String userFriendlyMessage) {
            this.userFriendlyMessage = userFriendlyMessage;
        }

        public String getUserFriendlyMessage() {
            return userFriendlyMessage;
        }
    }
}
