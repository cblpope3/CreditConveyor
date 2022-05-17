package ru.leonov.conveyor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.leonov.conveyor.dto.ModelsCreditDTO;
import ru.leonov.conveyor.dto.ModelsLoanOfferDTO;
import ru.leonov.conveyor.dto.ModelsPaymentScheduleElementDTO;

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

    private final Random randomGenerator = new Random();

    /**
     * This method calculating four not detailed credit offers to customer.
     *
     * @param creditAmount requested credit amount.
     * @param creditTerm   term of requested credit.
     * @param baseRate     base credit rate.
     * @return {@link List} of four credit offers.
     */
    public List<ModelsLoanOfferDTO> generateCreditOffers(BigDecimal creditAmount, int creditTerm,
                                                         BigDecimal baseRate) {

        log.trace("Generating credit pre-offer list...");
        ArrayList<ModelsLoanOfferDTO> resultList = new ArrayList<>(4);

        //generating result list with initial parameters
        for (int i = 0; i < 4; i++) {
            ModelsLoanOfferDTO modelsLoanOfferDTO = new ModelsLoanOfferDTO();
            //fixme application id random generation is temporary.
            modelsLoanOfferDTO.setApplicationId(randomGenerator.nextLong(1, 1000000));
            modelsLoanOfferDTO.requestedAmount(creditAmount);
            modelsLoanOfferDTO.setTerm(creditTerm);
            modelsLoanOfferDTO.setIsSalaryClient(false);
            modelsLoanOfferDTO.setIsInsuranceEnabled(false);
            modelsLoanOfferDTO.setRate(baseRate);

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
        if (log.isTraceEnabled()) log.trace("Generated offers: {}, {}, {}, {}",
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
    public ModelsCreditDTO calculateCredit(BigDecimal creditAmount, BigDecimal creditRate, int creditTerm,
                                           boolean isInsuranceEnabled, boolean isSalaryClient) {

        if (log.isTraceEnabled()) log.trace("Generating {} roubles {}% credit for {} months.",
                creditAmount, creditRate, creditRate);

        ModelsCreditDTO credit = new ModelsCreditDTO();

        credit.setAmount(creditAmount);
        credit.setTerm(creditTerm);
        credit.setRate(creditRate);
        credit.setIsInsuranceEnabled(isInsuranceEnabled);
        credit.setIsSalaryClient(isSalaryClient);

        // размер ежемесячного платежа(monthlyPayment),
        BigDecimal monthlyPayment = this.calculateMonthlyPayment(creditAmount, creditRate, creditTerm);
        // график ежемесячных платежей (List<PaymentScheduleElement>)
        List<ModelsPaymentScheduleElementDTO> paymentSchedule = this.calculateMonthlyPaymentsSchedule(
                creditAmount, creditTerm, creditRate.divide(BigDecimal.valueOf(1200), MathContext.DECIMAL64), monthlyPayment);
        BigDecimal psk = this.calculatePSK(paymentSchedule, creditAmount);

        credit.setMonthlyPayment(monthlyPayment);
        credit.setPaymentSchedule(paymentSchedule);
        credit.setPsk(psk);

        if (log.isTraceEnabled()) log.trace("Credit generated: {}", credit);

        return credit;
    }

    /**
     * Calculation of full credit price with simplified formula.
     *
     * @param creditAmount     full credit amount, roubles.
     * @param totalPayments    total payments for credit (debt and interest), roubles.
     * @param creditTermMonths credit term, months.
     * @return calculated full credit price.
     * @deprecated because calculation with this formula gives incorrect results.
     */
    @SuppressWarnings("unused")
    @Deprecated(since = "1.0", forRemoval = false)
    private BigDecimal calculatePSKSimplified(BigDecimal creditAmount, BigDecimal totalPayments, int creditTermMonths) {
        // PSK = 100 * ((s/s0) - 1) / n
        // where
        // s - total payments sum
        // s0 - credit amount
        // n - credit term in years
        log.info("Calculating PSK with simplified formula.");
        BigDecimal creditTermYears = BigDecimal.valueOf(creditTermMonths).divide(BigDecimal.valueOf(12), MathContext.DECIMAL64);

        BigDecimal overpayCoefficient = totalPayments.divide(creditAmount, MathContext.DECIMAL64).subtract(BigDecimal.ONE);
        BigDecimal partialResult = overpayCoefficient
                .divide(creditTermYears, MathContext.DECIMAL64);

        return partialResult.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculation of full credit price. Calculated by
     * <a href="https://unicom24.ru/media/open/2/8/7e/87ef428f1c52a440b97e516d7dbd06c2.jpg">this</a> formula.
     *
     * @param paymentSchedule {@link List} of calculated payments.
     * @param creditAmount    full credit amount.
     * @return calculated full credit price.
     */
    private BigDecimal calculatePSK(List<ModelsPaymentScheduleElementDTO> paymentSchedule,
                                    BigDecimal creditAmount) {

        log.trace("Calculating PSK.");

        //making dates of payment collection
        //first element is day of receipt of the loan, assuming that this is one month before first payment date.
        List<LocalDate> paymentDates = new ArrayList<>();
        paymentDates.add(paymentSchedule.get(0).getDate().minusMonths(1));
        paymentDates.addAll(paymentSchedule.stream()
                .map(ModelsPaymentScheduleElementDTO::getDate)
                .toList());

        //making list of all payments
        //first payment is negative credit amount (represents obtaining of the loan)
        List<BigDecimal> payments = new ArrayList<>();
        payments.add(creditAmount.multiply(BigDecimal.valueOf(-1)));
        payments.addAll(paymentSchedule.stream()
                .map(ModelsPaymentScheduleElementDTO::getTotalPayment)
                .toList());

        //calculating total payments amount (including getting of the loan itself)
        int paymentsNumber = payments.size();

        int basePeriod = 30;
        long basePeriodNumberInYear = Math.round(365.0 / basePeriod);

        //pre-calculation of days number from loan obtaining date to given payment date.
        List<Long> daysFromLoanObtained = new ArrayList<>();
        for (int k = 0; k < paymentsNumber; k++) {
            daysFromLoanObtained.add(paymentDates.get(0).until(paymentDates.get(k), ChronoUnit.DAYS));
        }

        //pre-calculation of Ek and Qk parameters for each payment
        List<BigDecimal> ek = new ArrayList<>();
        List<Long> qk = new ArrayList<>();

        for (int k = 0; k < paymentsNumber; k++) {
            //e[k] = (daysFromLoanObtained[k] % basePeriod) / basePeriod
            ek.add((BigDecimal.valueOf(daysFromLoanObtained.get(k))
                    .remainder(BigDecimal.valueOf(basePeriod)))
                    .divide(BigDecimal.valueOf(basePeriod), MathContext.DECIMAL64));

            //q[k] = Math.floor(daysFromLoanObtained[k] / basePeriod)
            qk.add(daysFromLoanObtained.get(k) / basePeriod);
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
                int iterationLimit = 10000;

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
        BigDecimal iPrev = BigDecimal.valueOf(1000);
        BigDecimal stepSize = BigDecimal.valueOf(0.01);
        BigDecimal resultTolerance = BigDecimal.valueOf(0.0001);

        //performing calculations of 'i' value with a successive increase in accuracy.
        //assuming that calculated value of 'i' is accurate enough when difference between new calculated 'i' and
        //it's previous value is less than required result tolerance (defined by resultTolerance variable).
        while (i.subtract(iPrev).abs().compareTo(resultTolerance) > 0) {
            iPrev = i;
            stepSize = stepSize.divide(BigDecimal.TEN, MathContext.DECIMAL64);
            i = new ICalculator().calculateNewIValue(stepSize, i);
        }

        //finally, calculating requested PSK.
        return i.multiply(BigDecimal.valueOf(basePeriodNumberInYear))
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculation of total credit payments (debt and interest) based on given payments schedule.
     *
     * @param paymentSchedule credit payments schedule.
     * @return sum of all credit payments.
     */
    private BigDecimal calculateTotalCreditPayments(List<ModelsPaymentScheduleElementDTO> paymentSchedule) {
        //assuming that in payments schedule can be the loan itself
        //therefore, counting only positive payments
        return paymentSchedule.stream()
                .map(ModelsPaymentScheduleElementDTO::getTotalPayment)
                .filter(payment -> payment.compareTo(BigDecimal.ZERO) > 0)
                .reduce(BigDecimal.valueOf(0),
                        BigDecimal::add);
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
    private List<ModelsPaymentScheduleElementDTO> calculateMonthlyPaymentsSchedule(
            BigDecimal creditAmount,
            int creditTerm,
            BigDecimal monthlyRate,
            BigDecimal monthlyPayment) {

        BigDecimal remainingDebt = BigDecimal.valueOf(creditAmount.doubleValue());

        List<ModelsPaymentScheduleElementDTO> paymentSchedule = new ArrayList<>(creditTerm);

        LocalDate paymentDate = LocalDate.now().plusMonths(1);

        for (int i = 1; i < creditTerm + 1; i++) {

            ModelsPaymentScheduleElementDTO paymentScheduleElement = new ModelsPaymentScheduleElementDTO();
            paymentScheduleElement.setNumber(i);
            paymentScheduleElement.setDate(paymentDate);

            paymentScheduleElement.setInterestPayment(this.calculateInterestPart(remainingDebt, monthlyRate));

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

        if (log.isTraceEnabled()) log.trace("Calculating monthly payment for {} roubles {}% credit for {} months.",
                creditAmount, yearlyCreditRate, creditTerm);

        //calculating monthlyCreditRate
        BigDecimal monthlyCreditRate = yearlyCreditRate.divide(BigDecimal.valueOf(1200), MathContext.DECIMAL64);

        // Для простоты восприятия принимаем overpayCoefficient = (1 + monthlyCreditRate)^creditTerm,
        BigDecimal overpayCoefficient = monthlyCreditRate.add(BigDecimal.valueOf(1), MathContext.DECIMAL64)
                .pow(creditTerm, MathContext.DECIMAL64);

        // Также принимаем partialResult = monthlyCreditRate/(overpayCoefficient - 1)
        BigDecimal partialResult = monthlyCreditRate
                .divide(overpayCoefficient.subtract(BigDecimal.valueOf(1), MathContext.DECIMAL64),
                        MathContext.DECIMAL64);

        // Тогда monthlyPayment = creditAmount*(monthlyCreditRate+partialResult)
        BigDecimal monthlyPayment = creditAmount.multiply(
                        monthlyCreditRate.add(partialResult, MathContext.DECIMAL64), MathContext.DECIMAL64)
                .setScale(2, RoundingMode.HALF_UP);

        if (log.isTraceEnabled()) log.trace("Calculated monthly payment is {} roubles.", monthlyPayment);

        return monthlyPayment;
    }
}
