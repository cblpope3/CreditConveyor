package ru.leonov.conveyor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.leonov.conveyor.dto.CreditDTO;
import ru.leonov.conveyor.dto.LoanOfferDTO;
import ru.leonov.conveyor.dto.PaymentScheduleElementDTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This service handle credit math calculations.
 */
@Slf4j
@Service
public class CreditCalculationService {

    private static final BigDecimal MONTHS_IN_YEAR = BigDecimal.valueOf(12);
    private static final BigDecimal HUNDRED_PERCENTS = BigDecimal.valueOf(100);
    private static final BigDecimal BASE_PERIOD = BigDecimal.valueOf(30);
    private static final BigDecimal DAYS_IN_YEAR = BigDecimal.valueOf(365);
    private static final BigDecimal INSURANCE_COST = BigDecimal.valueOf(100000);
    private static final int PSK_ITERATION_LIMIT = 10000;
    private static final BigDecimal PSK_INITIAL_STEP_SIZE = BigDecimal.valueOf(0.01);
    private static final BigDecimal PSK_RESULT_TOLERANCE = BigDecimal.valueOf(0.0001);

    private final Random randomGenerator = new Random();


    /**
     * This method calculating four not detailed credit offers to customer.
     *
     * @param creditAmount requested credit amount.
     * @param creditTerm   term of requested credit.
     * @param baseRate     base credit rate.
     * @return {@link List} of four credit offers.
     */
    public List<LoanOfferDTO> generateCreditOffers(BigDecimal creditAmount, int creditTerm,
                                                   BigDecimal baseRate) {

        log.trace("Generating credit pre-offer list...");
        ArrayList<LoanOfferDTO> resultList = new ArrayList<>(4);

        //generating result list with initial parameters
        for (int i = 0; i < 4; i++) {
            LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
            //fixme application id random generation is temporary.
            loanOfferDTO.setApplicationId(randomGenerator.nextLong(1, 1000000));
            loanOfferDTO.requestedAmount(creditAmount);
            loanOfferDTO.setTerm(creditTerm);
            loanOfferDTO.setIsSalaryClient(false);
            loanOfferDTO.setIsInsuranceEnabled(false);
            loanOfferDTO.setRate(baseRate);

            resultList.add(loanOfferDTO);
        }
        resultList.get(1).setIsInsuranceEnabled(true);
        resultList.get(2).setIsSalaryClient(true);
        resultList.get(3).setIsSalaryClient(true);
        resultList.get(3).setIsInsuranceEnabled(true);

        //calculating different offers
        for (LoanOfferDTO LoanOfferDTO : resultList) {

            BigDecimal paymentForInsurance = BigDecimal.ZERO;

            //calculating current rate depending on base rate and booleans
            if (Boolean.TRUE.equals(LoanOfferDTO.getIsInsuranceEnabled())) {
                LoanOfferDTO.setRate(LoanOfferDTO.getRate().subtract(BigDecimal.ONE));
                paymentForInsurance = INSURANCE_COST;
            }
            if (Boolean.TRUE.equals(LoanOfferDTO.getIsSalaryClient()))
                LoanOfferDTO.setRate(LoanOfferDTO.getRate().subtract(BigDecimal.valueOf(3)));

            //calculating monthly payment
            LoanOfferDTO.setMonthlyPayment(calculateMonthlyPayment(
                    LoanOfferDTO.getRequestedAmount(),
                    LoanOfferDTO.getRate(),
                    LoanOfferDTO.getTerm()));

            //calculating total credit amount
            LoanOfferDTO.setTotalAmount(LoanOfferDTO.getMonthlyPayment()
                    .multiply(BigDecimal.valueOf(LoanOfferDTO.getTerm()), MathContext.DECIMAL64)
                    .add(paymentForInsurance));
        }
        log.trace("Generated offers: {}, {}, {}, {}",
                resultList.get(0).toString(),
                resultList.get(1).toString(),
                resultList.get(2).toString(),
                resultList.get(3).toString());
        return resultList;
    }

    /**
     * Making detailed credit offer.
     *
     * @param creditAmount       amount of renting money.
     * @param creditRate         credit rate.
     * @param creditTerm         term of credit.
     * @param isInsuranceEnabled is insurance exist.
     * @param isSalaryClient     is customer salary client.
     * @return detailed credit offer.
     */
    public CreditDTO calculateCredit(BigDecimal creditAmount, BigDecimal creditRate, int creditTerm,
                                     boolean isInsuranceEnabled, boolean isSalaryClient) {

        log.trace("Generating {} roubles {}% credit for {} months.",
                creditAmount, creditRate, creditRate);

        CreditDTO credit = new CreditDTO();

        credit.setAmount(creditAmount);
        credit.setTerm(creditTerm);
        credit.setRate(creditRate);
        credit.setIsInsuranceEnabled(isInsuranceEnabled);
        credit.setIsSalaryClient(isSalaryClient);

        // размер ежемесячного платежа(monthlyPayment),
        BigDecimal monthlyPayment = calculateMonthlyPayment(creditAmount, creditRate, creditTerm);
        // график ежемесячных платежей (List<PaymentScheduleElement>)
        List<PaymentScheduleElementDTO> paymentSchedule = calculateMonthlyPaymentsSchedule(
                creditAmount, creditTerm,
                creditRate.divide(MONTHS_IN_YEAR.multiply(HUNDRED_PERCENTS), MathContext.DECIMAL64), monthlyPayment);
        BigDecimal psk = calculatePSK(paymentSchedule, creditAmount);

        credit.setMonthlyPayment(monthlyPayment);
        credit.setPaymentSchedule(paymentSchedule);
        credit.setPsk(psk);

        log.trace("Credit generated: {}", credit);

        return credit;
    }

    /**
     * Calculation of full credit price. Calculated by
     * <a href="https://unicom24.ru/media/open/2/8/7e/87ef428f1c52a440b97e516d7dbd06c2.jpg">this</a> formula.
     *
     * @param paymentSchedule {@link List} of calculated payments.
     * @param creditAmount    full credit amount.
     * @return calculated full credit price.
     */
    private BigDecimal calculatePSK(List<PaymentScheduleElementDTO> paymentSchedule,
                                    BigDecimal creditAmount) {

        log.trace("Calculating PSK.");

        //making dates of payment collection
        //first element is day of receipt of the loan, assuming that this is one month before first payment date.
        List<LocalDate> paymentDates = new ArrayList<>();
        paymentDates.add(paymentSchedule.get(0).getDate().minusMonths(1));
        paymentDates.addAll(paymentSchedule.stream()
                .map(PaymentScheduleElementDTO::getDate)
                .toList());

        //making list of all payments
        //first payment is negative credit amount (represents obtaining of the loan)
        List<BigDecimal> payments = new ArrayList<>();
        payments.add(creditAmount.multiply(BigDecimal.valueOf(-1)));
        payments.addAll(paymentSchedule.stream()
                .map(PaymentScheduleElementDTO::getTotalPayment)
                .toList());

        //calculating total payments amount (including getting of the loan itself)
        int paymentsNumber = payments.size();


        BigDecimal basePeriodNumberInYear = DAYS_IN_YEAR.divide(BASE_PERIOD, MathContext.DECIMAL64)
                .setScale(0, RoundingMode.HALF_UP);


        //pre-calculation of days number from loan obtaining date to given payment date.
        List<Long> daysFromLoanObtained = new ArrayList<>();
        for (int k = 0; k < paymentsNumber; k++) {
            daysFromLoanObtained.add(paymentDates.get(0).until(paymentDates.get(k), ChronoUnit.DAYS));
        }

        //pre-calculation of Ek and Qk parameters for each payment
        List<BigDecimal> ek = new ArrayList<>();
        List<Long> qk = new ArrayList<>();

        for (int k = 0; k < paymentsNumber; k++) {
            //e[k] = (daysFromLoanObtained[k] % BASE_PERIOD) / BASE_PERIOD
            ek.add((BigDecimal.valueOf(daysFromLoanObtained.get(k))
                    .remainder(BASE_PERIOD))
                    .divide(BASE_PERIOD, MathContext.DECIMAL64));

            //q[k] = Math.floor(daysFromLoanObtained[k] / BASE_PERIOD)
            qk.add(daysFromLoanObtained.get(k) / BASE_PERIOD.intValue());
        }

        //calculation of 'i' coefficient (base period percent rate, whatever it means).
        class ICalculator {

            /**
             * Method that calculates value of i. Contains 'while' loop that perform approximation to 'i' value, that
             * is producing value of sum as close to zero as possible with given step size.
             * @param stepSize increment amount of 'i' on every iteration step.
             * @param i initial value of 'i'.
             * @return value of 'i' that produce closest to zero value of sum.
             */
            BigDecimal calculateNewIValue(BigDecimal stepSize, BigDecimal i) {
                BigDecimal zeroSum = BigDecimal.ONE;
                int iterationLimit = PSK_ITERATION_LIMIT;

                while (zeroSum.compareTo(BigDecimal.ZERO) > 0) {
                    --iterationLimit;
                    zeroSum = BigDecimal.ZERO;
                    i = i.add(stepSize);

                    for (int k = 0; k < paymentsNumber; k++) {
                        //zeroSum += (payments[k] / ((1 + ek[k] * i) * (1 + i) ^ qk[k]))
                        BigDecimal ekComponent = ek.get(k).multiply(i).add(BigDecimal.ONE);
                        BigDecimal qkComponent = i.add(BigDecimal.ONE).pow(qk.get(k).intValue(), MathContext.DECIMAL64);
                        zeroSum = zeroSum.add(
                                payments.get(k).divide(ekComponent.multiply(qkComponent), MathContext.DECIMAL64));
                    }

                    if (iterationLimit < 0) {
                        log.error("PSK calculation error. Iteration limit reached.");
                        throw new ArithmeticException("Can't calculate PSK: iteration limit reached!");
                    }
                }

                return i;
            }
        }

        BigDecimal i = BigDecimal.ZERO;
        BigDecimal iPrev = BigDecimal.valueOf(1000); //any value that not equal to i
        BigDecimal stepSize = PSK_INITIAL_STEP_SIZE;

        //performing calculations of 'i' value with a successive increase in accuracy.
        //assuming that calculated value of 'i' is accurate enough when difference between new calculated 'i' and
        //it's previous value is less than required result tolerance (defined by resultTolerance variable).
        while (i.subtract(iPrev).abs().compareTo(PSK_RESULT_TOLERANCE) > 0) {
            iPrev = i;
            stepSize = stepSize.divide(BigDecimal.TEN, MathContext.DECIMAL64);
            i = new ICalculator().calculateNewIValue(stepSize, i);
        }

        //finally, calculating requested PSK.
        return i.multiply(basePeriodNumberInYear)
                .multiply(HUNDRED_PERCENTS)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Generation credit payment schedule.
     *
     * @param creditAmount   overall credit amount.
     * @param creditTerm     overall credit term.
     * @param monthlyRate    monthly credit rate.
     * @param monthlyPayment monthly payment.
     * @return {@link List} of payments.
     */
    private List<PaymentScheduleElementDTO> calculateMonthlyPaymentsSchedule(
            BigDecimal creditAmount,
            int creditTerm,
            BigDecimal monthlyRate,
            BigDecimal monthlyPayment) {

        BigDecimal remainingDebt = BigDecimal.valueOf(creditAmount.doubleValue());

        List<PaymentScheduleElementDTO> paymentSchedule = new ArrayList<>(creditTerm);

        LocalDate paymentDate = LocalDate.now().plusMonths(1);

        for (int i = 1; i < creditTerm + 1; i++) {

            PaymentScheduleElementDTO paymentScheduleElement = new PaymentScheduleElementDTO();
            paymentScheduleElement.setNumber(i);
            paymentScheduleElement.setDate(paymentDate);

            paymentScheduleElement.setInterestPayment(calculateInterestPart(remainingDebt, monthlyRate));

            if (i == creditTerm) {
                //if it's last payment - pay all remaining debt
                paymentScheduleElement.setDebtPayment(remainingDebt);
            } else {
                //debt payment is monthly_payment - interest_payment
                paymentScheduleElement.setDebtPayment(
                        monthlyPayment.subtract(paymentScheduleElement.getInterestPayment(), MathContext.DECIMAL64));
            }
            paymentScheduleElement.setTotalPayment(
                    paymentScheduleElement.getInterestPayment()
                            .add(paymentScheduleElement.getDebtPayment(), MathContext.DECIMAL64));

            remainingDebt = remainingDebt.subtract(paymentScheduleElement.getDebtPayment(), MathContext.DECIMAL64);
            paymentScheduleElement.setRemainingDebt(remainingDebt);

            paymentSchedule.add(paymentScheduleElement);
            paymentDate = paymentDate.plusMonths(1);
        }

        return paymentSchedule;
    }

    /**
     * Calculation of interest payment part based on remaining debt. Formula taken from
     * <a href="https://www.raiffeisen.ru/wiki/kak-rasschitat-procenty-po-kreditu/">Raiffeisen bank web-site</a>
     *
     * @param remainingDebt remaining credit debt.
     * @param monthlyRate   monthly credit rate.
     * @return bank interest part of month payment.
     */
    private BigDecimal calculateInterestPart(BigDecimal remainingDebt, BigDecimal monthlyRate) {
        // I = S * P
        // S - remaining debt
        // P - monthly credit rate
        // I - interest part
        return remainingDebt.multiply(monthlyRate, MathContext.DECIMAL64).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Method calculate monthly payment. Formula taken from
     * <a href="https://www.raiffeisen.ru/wiki/kak-rasschitat-procenty-po-kreditu/">Raiffeisen bank web-site</a>
     *
     * @param creditAmount     credit amount.
     * @param yearlyCreditRate credit rate.
     * @param creditTerm       credit term.
     * @return monthly payment.
     */
    private BigDecimal calculateMonthlyPayment(BigDecimal creditAmount, BigDecimal yearlyCreditRate, int creditTerm) {

        // Размер ежемесячного платежа =
        // creditAmount*(monthlyCreditRate+(monthlyCreditRate/(1+monthlyCreditRate)^creditTerm-1))
        // где
        // creditAmount — сумма займа
        // monthlyCreditRate — ставка процента за один месяц
        // creditTerm — срок кредитования.

        log.trace("Calculating monthly payment for {} roubles {}% credit for {} months.",
                creditAmount, yearlyCreditRate, creditTerm);

        //calculating monthlyCreditRate
        BigDecimal monthlyCreditRate = yearlyCreditRate.divide(MONTHS_IN_YEAR.multiply(HUNDRED_PERCENTS), MathContext.DECIMAL64);

        // Для простоты восприятия принимаем overpayCoefficient = (1 + monthlyCreditRate)^creditTerm,
        BigDecimal overpayCoefficient = monthlyCreditRate.add(BigDecimal.ONE, MathContext.DECIMAL64)
                .pow(creditTerm, MathContext.DECIMAL64);

        // Также принимаем partialResult = monthlyCreditRate/(overpayCoefficient - 1)
        BigDecimal partialResult = monthlyCreditRate
                .divide(overpayCoefficient.subtract(BigDecimal.ONE, MathContext.DECIMAL64),
                        MathContext.DECIMAL64);

        // Тогда monthlyPayment = creditAmount*(monthlyCreditRate+partialResult)
        BigDecimal monthlyPayment = creditAmount.multiply(
                        monthlyCreditRate.add(partialResult, MathContext.DECIMAL64), MathContext.DECIMAL64)
                .setScale(2, RoundingMode.HALF_UP);

        log.trace("Calculated monthly payment is {} roubles.", monthlyPayment);

        return monthlyPayment;
    }
}
