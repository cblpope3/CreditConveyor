package ru.leonov.conveyor.service;

import org.springframework.stereotype.Service;
import ru.leonov.conveyor.dto.ModelsCreditDTO;
import ru.leonov.conveyor.dto.ModelsLoanOfferDTO;
import ru.leonov.conveyor.dto.ModelsPaymentScheduleElementDTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This service handle credit math calculations.
 */
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
        BigDecimal psk = this.calculatePSK(paymentSchedule, creditAmount, creditTerm);

        credit.setMonthlyPayment(monthlyPayment);
        credit.setPaymentSchedule(paymentSchedule);
        credit.setPsk(psk);

        return credit;
    }

    /**
     * Calculation of full credit price.
     *
     * @param paymentSchedule {@link List} of calculated payments.
     * @param creditAmount    full credit amount.
     * @param creditTerm      credit term in months.
     * @return calculated full credit price.
     */
    private BigDecimal calculatePSK(List<ModelsPaymentScheduleElementDTO> paymentSchedule,
                                    BigDecimal creditAmount, int creditTerm) {

        // PSK = 12 * 100 * ((s/s0) - 1) / n
        // where
        // s - total payments sum
        // s0 - credit amount
        // n - number of months of credit term

        //fixme calculation result is not matching with online-calculator result
        BigDecimal totalPaymentsSum = paymentSchedule.stream()
                .map(ModelsPaymentScheduleElementDTO::getTotalPayment)
                .reduce(BigDecimal.valueOf(0),
                        BigDecimal::add);

        BigDecimal overpayCoefficient = totalPaymentsSum.divide(creditAmount, MathContext.DECIMAL64);
        BigDecimal partialResult = overpayCoefficient.subtract(BigDecimal.valueOf(1))
                .divide(BigDecimal.valueOf(creditTerm), MathContext.DECIMAL64);

        return partialResult.multiply(BigDecimal.valueOf(1200)).setScale(2, RoundingMode.HALF_UP);
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
        return creditAmount.multiply(
                        monthlyCreditRate.add(partialResult, MathContext.DECIMAL64), MathContext.DECIMAL64)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
