package ru.leonov.conveyor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.leonov.conveyor.dto.CreditDTO;
import ru.leonov.conveyor.dto.PaymentScheduleElementDTO;
import ru.leonov.conveyor.test_data.LoanCalculationTestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class CalculateCreditTest {

    private CreditCalculationService creditCalculationService;

    @BeforeEach
    void SetUp() {
        creditCalculationService = new CreditCalculationService();
    }

    //testing credit offers generation
    @Test
    void generateCreditOffers() {
        //simulating service method call
        CreditDTO calculatedCredit = creditCalculationService.calculateCredit(
                LoanCalculationTestData.getFineLoanCalculationRequest().getAmount(),
                LoanCalculationTestData.getFineLoanCalculationResponse().getRate(),
                LoanCalculationTestData.getFineLoanCalculationRequest().getTerm(),
                LoanCalculationTestData.getFineLoanCalculationRequest().getIsInsuranceEnabled(),
                LoanCalculationTestData.getFineLoanCalculationRequest().getIsSalaryClient());

        //checking that response is correct
        assertTrue(creditsAreEqual(LoanCalculationTestData.getFineLoanCalculationResponse(), calculatedCredit));
    }

    private boolean creditsAreEqual(CreditDTO credit1, CreditDTO credit2) {

        return
                credit1.getAmount().compareTo(credit2.getAmount()) == 0 &&
                        credit1.getMonthlyPayment().compareTo(credit2.getMonthlyPayment()) == 0 &&
                        credit1.getRate().compareTo(credit2.getRate()) == 0 &&
                        credit1.getPsk().compareTo(credit2.getPsk()) == 0 &&
                        credit1.getTerm().equals(credit2.getTerm()) &&
                        credit1.getIsInsuranceEnabled().equals(credit2.getIsInsuranceEnabled()) &&
                        credit1.getIsSalaryClient().equals(credit2.getIsSalaryClient()) &&
                        creditSchedulesAreEqual(credit1.getPaymentSchedule(), credit2.getPaymentSchedule());

    }

    private boolean creditSchedulesAreEqual(List<PaymentScheduleElementDTO> paymentSchedule1,
                                            List<PaymentScheduleElementDTO> paymentSchedule2) {

        if (paymentSchedule1.size() != paymentSchedule2.size()) return false;

        for (int i = 0; i < paymentSchedule1.size(); i++) {
            if (
                    paymentSchedule1.get(i).getTotalPayment().compareTo(paymentSchedule2.get(i).getTotalPayment()) != 0 ||
                            paymentSchedule1.get(i).getInterestPayment().compareTo(paymentSchedule2.get(i).getInterestPayment()) != 0 ||
                            paymentSchedule1.get(i).getDebtPayment().compareTo(paymentSchedule2.get(i).getDebtPayment()) != 0 ||
                            paymentSchedule1.get(i).getRemainingDebt().compareTo(paymentSchedule2.get(i).getRemainingDebt()) != 0 ||
                            !paymentSchedule1.get(i).getNumber().equals(paymentSchedule2.get(i).getNumber()) ||
                            !paymentSchedule1.get(i).getDate().equals(paymentSchedule2.get(i).getDate())
            ) return false;
        }

        return true;
    }
}