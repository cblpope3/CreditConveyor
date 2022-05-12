package ru.leonov.conveyor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.leonov.conveyor.dto.ModelsLoanApplicationRequestDTO;
import ru.leonov.conveyor.dto.ModelsLoanOfferDTO;
import ru.leonov.conveyor.exceptions.LoanRequestException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Service that perform pre-scoring job.
 */
@Service
@SuppressWarnings("unused")
public class PreScoringService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final double baseRate;

    private final Random randomGenerator = new Random();

    public PreScoringService(@Value("${app-params.baseRate}") double baseRate) {
        this.baseRate = baseRate;
    }

    /**
     * This method calculating four credit offers to customer.
     *
     * @param loanRequest requested credit.
     * @return {@link List} of four credit offers.
     * @throws LoanRequestException if request not passed pre-verification process.
     */
    public List<ModelsLoanOfferDTO> getCreditOfferList(ModelsLoanApplicationRequestDTO loanRequest) throws LoanRequestException {

        //checking request. exception will be thrown if issues happened.
        this.validateLoanApplicationRequestDTO(loanRequest);

        ArrayList<ModelsLoanOfferDTO> resultList = new ArrayList<>(4);

        //generating result list with initial parameters
        for (int i = 0; i < 4; i++) {
            ModelsLoanOfferDTO modelsLoanOfferDTO = new ModelsLoanOfferDTO();
            //fixme application id random generation is temporary.
            modelsLoanOfferDTO.setApplicationId(randomGenerator.nextLong(1, 1000000));
            modelsLoanOfferDTO.requestedAmount(loanRequest.getAmount());
            modelsLoanOfferDTO.setTerm(loanRequest.getTerm());
            modelsLoanOfferDTO.setIsSalaryClient(false);
            modelsLoanOfferDTO.setIsInsuranceEnabled(false);
            modelsLoanOfferDTO.setRate(BigDecimal.valueOf(baseRate));

            resultList.add(modelsLoanOfferDTO);
        }
        resultList.get(1).setIsInsuranceEnabled(true);
        resultList.get(2).setIsSalaryClient(true);
        resultList.get(3).setIsSalaryClient(true);
        resultList.get(3).setIsInsuranceEnabled(true);

        //calculating different offers
        for (ModelsLoanOfferDTO modelsLoanOfferDTO : resultList) {

            double paymentForInsurance = 0;

            //calculating current rate depending on base rate and booleans
            if (Boolean.TRUE.equals(modelsLoanOfferDTO.getIsInsuranceEnabled())) {
                modelsLoanOfferDTO.setRate(modelsLoanOfferDTO.getRate().subtract(BigDecimal.valueOf(1)));
                paymentForInsurance = 100000;
            }
            if (Boolean.TRUE.equals(modelsLoanOfferDTO.getIsSalaryClient()))
                modelsLoanOfferDTO.setRate(modelsLoanOfferDTO.getRate().subtract(BigDecimal.valueOf(3)));

            //calculating monthly payment
            modelsLoanOfferDTO.setMonthlyPayment(this.calculateMonthlyPayment(
                    modelsLoanOfferDTO.getRequestedAmount(),
                    modelsLoanOfferDTO.getRate(),
                    modelsLoanOfferDTO.getTerm()));

            //calculating total credit amount
            modelsLoanOfferDTO.setTotalAmount(modelsLoanOfferDTO.getMonthlyPayment()
                    .multiply(BigDecimal.valueOf(modelsLoanOfferDTO.getTerm()), MathContext.DECIMAL64)
                    .add(BigDecimal.valueOf(paymentForInsurance)));
        }

        return resultList;
    }

    /**
     * Method calculate monthly payment. <b>This method is not accurate due it's not counting number of days in
     * each month.</b>
     *
     * @param creditAmount  credit amount.
     * @param creditRate    credit rate.
     * @param paymentNumber credit term.
     * @return monthly payment.
     */
    private BigDecimal calculateMonthlyPayment(BigDecimal creditAmount, BigDecimal creditRate, int paymentNumber) {
        BigDecimal monthlyCreditRate = creditRate.divide(BigDecimal.valueOf(1200), MathContext.DECIMAL64);
        BigDecimal partialResult = monthlyCreditRate
                .add(BigDecimal.valueOf(1))
                .pow(paymentNumber, MathContext.DECIMAL64);

        BigDecimal annualCoefficient = partialResult.multiply(monthlyCreditRate)
                .divide(partialResult.subtract(BigDecimal.valueOf(1)), MathContext.DECIMAL64);

        return creditAmount.multiply(annualCoefficient, MathContext.DECIMAL64);
    }


    /**
     * Method checks that loan request has valid format.
     *
     * @param loanRequest loan request from user.
     * @throws LoanRequestException if one of required parameters is null or parameters have incorrect format.
     * @see LoanRequestException.ExceptionCause
     */
    private void validateLoanApplicationRequestDTO(ModelsLoanApplicationRequestDTO loanRequest) throws LoanRequestException {

        if (logger.isTraceEnabled()) logger.trace("Validating loan request...");

        this.parametersNullCheck(loanRequest);

        // First and last name - from 2 to 30 english letters, not null.
        // Middle name - from 2 to 30 english letters, nullable.
        this.validateName(loanRequest.getFirstName());
        this.validateName(loanRequest.getLastName());
        if (loanRequest.getMiddleName() != null)
            this.validateName(loanRequest.getMiddleName());

        this.validateCreditAmount(loanRequest.getAmount());
        this.validateCreditTerm(loanRequest.getTerm());
        this.validateCustomerAge(loanRequest.getBirthdate());
        this.validateEmail(loanRequest.getEmail());
        this.validatePassportSeries(loanRequest.getPassportSeries());
        this.validatePassportNumber(loanRequest.getPassportNumber());

        if (logger.isTraceEnabled()) logger.trace("Loan request is valid.");
    }

    /**
     * Method performs null-check of {@link ModelsLoanApplicationRequestDTO} required parameters.
     *
     * @param loanRequest incoming loan request.
     * @throws LoanRequestException if one of required parameters is null.
     */
    private void parametersNullCheck(ModelsLoanApplicationRequestDTO loanRequest) throws LoanRequestException {

        if (loanRequest.getAmount() == null ||
                loanRequest.getTerm() == null ||
                loanRequest.getFirstName() == null ||
                loanRequest.getLastName() == null ||
                loanRequest.getEmail() == null ||
                loanRequest.getBirthdate() == null ||
                loanRequest.getPassportNumber() == null ||
                loanRequest.getPassportSeries() == null) {
            if (logger.isDebugEnabled()) logger.debug("One of loan request required parameters is null.");
            throw new LoanRequestException(LoanRequestException.ExceptionCause.EMPTY_REQUIRED_PARAMETER);
        }
    }

    /**
     * Method to validate first, second and middle names. Name length must be between 2 and 30 characters and contain
     * only english letters.
     *
     * @param name {@link String} to check.
     * @throws LoanRequestException if name is not valid.
     */
    private void validateName(String name) throws LoanRequestException {

        if (!name.matches("[a-zA-Z]{2,30}")) {
            if (logger.isDebugEnabled()) logger.debug("One of 'name' fields is not valid: {}.", name);
            throw new LoanRequestException(LoanRequestException.ExceptionCause.INCORRECT_NAME, name);
        }
    }

    /**
     * Validation of requested credit amount.
     *
     * @param requestedAmount requested credit amount.
     * @throws LoanRequestException if requested credit amount is less than 10000.
     */
    private void validateCreditAmount(BigDecimal requestedAmount) throws LoanRequestException {
        if (requestedAmount.compareTo(BigDecimal.valueOf(10000)) < 0) {
            LoanRequestException exception = new LoanRequestException(
                    LoanRequestException.ExceptionCause.INCORRECT_CREDIT_AMOUNT,
                    requestedAmount.toString());
            if (logger.isDebugEnabled()) logger.debug(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Validation term of requested credit.
     *
     * @param creditTerm term of requested credit.
     * @throws LoanRequestException if term of requested credit is less than 6 months.
     */
    private void validateCreditTerm(Integer creditTerm) throws LoanRequestException {
        if (creditTerm < 6) {
            LoanRequestException exception = new LoanRequestException(
                    LoanRequestException.ExceptionCause.INCORRECT_CREDIT_TERM,
                    creditTerm.toString());
            if (logger.isDebugEnabled()) logger.debug(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Validation of customers age.
     *
     * @param birthday customers birth-date.
     * @throws LoanRequestException if customer is younger than 18 years.
     */
    private void validateCustomerAge(LocalDate birthday) throws LoanRequestException {
        Period period = Period.between(birthday, LocalDate.now());
        if (period.getYears() < 18) {
            LoanRequestException exception = new LoanRequestException(
                    LoanRequestException.ExceptionCause.PERSON_TOO_YOUNG,
                    String.valueOf(period.getYears()));
            if (logger.isDebugEnabled()) logger.debug(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Validate customers e-mail address.
     *
     * @param email customers e-mail.
     * @throws LoanRequestException if e-mail not match required pattern.
     */
    private void validateEmail(String email) throws LoanRequestException {
        if (!email.matches("[\\w\\.]{2,50}@[\\w\\.]{2,20}")) {
            LoanRequestException exception = new LoanRequestException(
                    LoanRequestException.ExceptionCause.INCORRECT_EMAIL,
                    email);

            if (logger.isDebugEnabled()) logger.debug(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Validation of passport series.
     *
     * @param series customers passport series.
     * @throws LoanRequestException if passport series not match required pattern.
     */
    private void validatePassportSeries(String series) throws LoanRequestException {
        if (!series.matches("[\\d]{4}")) {
            LoanRequestException exception = new LoanRequestException(
                    LoanRequestException.ExceptionCause.INCORRECT_PASSPORT_SERIES,
                    series);
            if (logger.isDebugEnabled()) logger.debug(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Validation of passport number.
     *
     * @param number customers passport number.
     * @throws LoanRequestException if passport number not match required pattern.
     */
    private void validatePassportNumber(String number) throws LoanRequestException {
        if (!number.matches("[\\d]{6}")) {
            LoanRequestException exception = new LoanRequestException(
                    LoanRequestException.ExceptionCause.INCORRECT_PASSPORT_NUMBER,
                    number);
            if (logger.isDebugEnabled()) logger.debug(exception.getMessage());
            throw exception;
        }
    }
}
