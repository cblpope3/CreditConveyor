package ru.leonov.conveyor.service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.leonov.conveyor.dto.LoanApplicationRequestDTO;
import ru.leonov.conveyor.dto.LoanOfferDTO;
import ru.leonov.conveyor.exceptions.LoanRequestException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Service that perform pre-scoring job.
 */
@Slf4j
@Service
public class PreScoringService {

    private static final BigDecimal MIN_CREDIT_AMOUNT = BigDecimal.valueOf(10000);
    private static final Integer MIN_CREDIT_TERM = 6;
    private static final Integer MIN_AGE = 18;

    private final CreditCalculationService creditCalculationService;
    private final BigDecimal baseRate;

    @Autowired
    public PreScoringService(@Value("${app-params.baseRate}") double baseRate,
                             CreditCalculationService creditCalculationService) {
        this.baseRate = BigDecimal.valueOf(baseRate);
        this.creditCalculationService = creditCalculationService;
    }

    /**
     * This method calculating four credit offers to customer.
     *
     * @param loanRequest requested credit.
     * @return {@link List} of four credit offers.
     * @throws LoanRequestException if request not passed pre-verification process.
     */
    public List<LoanOfferDTO> getCreditOfferList(LoanApplicationRequestDTO loanRequest) throws LoanRequestException {

        //checking request. exception will be thrown if issues happened.
        validateLoanApplicationRequestDTO(loanRequest);

        return creditCalculationService.generateCreditOffers(loanRequest.getAmount(), loanRequest.getTerm(),
                baseRate);

    }

    /**
     * Method checks that loan request has valid format.
     *
     * @param loanRequest loan request from user.
     * @throws LoanRequestException if one of required parameters is null or parameters have incorrect format.
     * @see LoanRequestException.ExceptionCause
     */
    private void validateLoanApplicationRequestDTO(LoanApplicationRequestDTO loanRequest) throws LoanRequestException {

        if (log.isTraceEnabled()) log.trace("Validating loan request...");

        try {
            // First and last name - from 2 to 30 english letters, not null.
            // Middle name - from 2 to 30 english letters, nullable.
            validateName(loanRequest.getFirstName());
            validateName(loanRequest.getLastName());
            if (loanRequest.getMiddleName() != null)
                validateName(loanRequest.getMiddleName());

            validateCreditAmount(loanRequest.getAmount());
            validateCreditTerm(loanRequest.getTerm());
            validateCustomerAge(loanRequest.getBirthdate());
            validateEmail(loanRequest.getEmail());
            validatePassportSeries(loanRequest.getPassportSeries());
            validatePassportNumber(loanRequest.getPassportNumber());
        } catch (NullPointerException e) {
            // assuming that this exception is thrown by lombok @NotNull annotation and means that some required
            // parameter is missing in request
            log.debug(e.getMessage());
            throw new LoanRequestException(LoanRequestException.ExceptionCause.EMPTY_REQUIRED_PARAMETER);
        }

        if (log.isTraceEnabled()) log.trace("Loan request is valid.");
    }

    /**
     * Method to validate first, second and middle names. Name length must be between 2 and 30 characters and contain
     * only english letters.
     *
     * @param name {@link String} to check.
     * @throws LoanRequestException if name is not valid.
     */
    private void validateName(@NonNull String name) throws LoanRequestException {

        if (!name.matches("[a-zA-Z]{2,30}")) {
            if (log.isDebugEnabled()) log.debug("One of 'name' fields is not valid: {}.", name);
            throw new LoanRequestException(LoanRequestException.ExceptionCause.INCORRECT_NAME, name);
        }
    }

    /**
     * Validation of requested credit amount.
     *
     * @param requestedAmount requested credit amount.
     * @throws LoanRequestException if requested credit amount is less than 10000.
     */
    private void validateCreditAmount(@NonNull BigDecimal requestedAmount) throws LoanRequestException {
        if (requestedAmount.compareTo(MIN_CREDIT_AMOUNT) < 0) {
            if (log.isDebugEnabled())
                log.debug(LoanRequestException.ExceptionCause.INCORRECT_CREDIT_AMOUNT.getUserFriendlyMessage());
            throw new LoanRequestException(LoanRequestException.ExceptionCause.INCORRECT_CREDIT_AMOUNT,
                    requestedAmount.toString());
        }
    }

    /**
     * Validation term of requested credit.
     *
     * @param creditTerm term of requested credit.
     * @throws LoanRequestException if term of requested credit is less than 6 months.
     */
    private void validateCreditTerm(@NonNull Integer creditTerm) throws LoanRequestException {
        if (creditTerm < MIN_CREDIT_TERM) {
            if (log.isDebugEnabled())
                log.debug(LoanRequestException.ExceptionCause.INCORRECT_CREDIT_TERM.getUserFriendlyMessage());
            throw new LoanRequestException(LoanRequestException.ExceptionCause.INCORRECT_CREDIT_TERM,
                    creditTerm.toString());
        }
    }

    /**
     * Validation of customers age.
     *
     * @param birthday customers birth-date.
     * @throws LoanRequestException if customer is younger than 18 years.
     */
    private void validateCustomerAge(@NonNull LocalDate birthday) throws LoanRequestException {
        Period period = Period.between(birthday, LocalDate.now());
        if (period.getYears() < MIN_AGE) {
            if (log.isDebugEnabled())
                log.debug(LoanRequestException.ExceptionCause.PERSON_TOO_YOUNG.getUserFriendlyMessage());
            throw new LoanRequestException(LoanRequestException.ExceptionCause.PERSON_TOO_YOUNG,
                    String.valueOf(period.getYears()));
        }
    }

    /**
     * Validate customers e-mail address.
     *
     * @param email customers e-mail.
     * @throws LoanRequestException if e-mail not match required pattern.
     */
    private void validateEmail(@NonNull String email) throws LoanRequestException {
        if (!email.matches("[\\w\\.]{2,50}@[\\w\\.]{2,20}")) {

            if (log.isDebugEnabled())
                log.debug(LoanRequestException.ExceptionCause.INCORRECT_EMAIL.getUserFriendlyMessage());
            throw new LoanRequestException(LoanRequestException.ExceptionCause.INCORRECT_EMAIL, email);
        }
    }

    /**
     * Validation of passport series.
     *
     * @param series customers passport series.
     * @throws LoanRequestException if passport series not match required pattern.
     */
    private void validatePassportSeries(@NonNull String series) throws LoanRequestException {
        if (!series.matches("[\\d]{4}")) {
            if (log.isDebugEnabled())
                log.debug(LoanRequestException.ExceptionCause.INCORRECT_PASSPORT_SERIES.getUserFriendlyMessage());
            throw new LoanRequestException(LoanRequestException.ExceptionCause.INCORRECT_PASSPORT_SERIES, series);
        }
    }

    /**
     * Validation of passport number.
     *
     * @param number customers passport number.
     * @throws LoanRequestException if passport number not match required pattern.
     */
    private void validatePassportNumber(@NonNull String number) throws LoanRequestException {
        if (!number.matches("[\\d]{6}")) {
            if (log.isDebugEnabled())
                log.debug(LoanRequestException.ExceptionCause.INCORRECT_PASSPORT_NUMBER.getUserFriendlyMessage());
            throw new LoanRequestException(LoanRequestException.ExceptionCause.INCORRECT_PASSPORT_NUMBER, number);
        }
    }
}
