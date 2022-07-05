package ru.leonov.conveyor.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.leonov.conveyor.dto.CreditDTO;
import ru.leonov.conveyor.dto.PaymentScheduleElementDTO;
import ru.leonov.conveyor.test_data.LoanCalculationTestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
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
                LoanCalculationTestData.getFineLoanCalculationRequestObject().getAmount(),
                LoanCalculationTestData.getFineLoanCalculationResponseObject().getRate(),
                LoanCalculationTestData.getFineLoanCalculationRequestObject().getTerm(),
                LoanCalculationTestData.getFineLoanCalculationRequestObject().getIsInsuranceEnabled(),
                LoanCalculationTestData.getFineLoanCalculationRequestObject().getIsSalaryClient());

        CreditDTO expectedCredit = LoanCalculationTestData.getFineLoanCalculationResponseObject();

        //checking that response is correct

        assertEquals(0, calculatedCredit.getAmount().compareTo(expectedCredit.getAmount()));
        assertEquals(0, calculatedCredit.getMonthlyPayment().compareTo(expectedCredit.getMonthlyPayment()));
        assertEquals(0, calculatedCredit.getRate().compareTo(expectedCredit.getRate()));
        assertEquals(0, calculatedCredit.getPsk().compareTo(expectedCredit.getPsk()));
        assertEquals(calculatedCredit.getTerm(), expectedCredit.getTerm());
        assertEquals(calculatedCredit.getIsInsuranceEnabled(), expectedCredit.getIsInsuranceEnabled());
        assertEquals(calculatedCredit.getIsSalaryClient(), expectedCredit.getIsSalaryClient());

        assertTrue(creditSchedulesAreEqual(calculatedCredit.getPaymentSchedule(), expectedCredit.getPaymentSchedule()));
    }

    /**
     * Method checks that two given credit payment schedules are equal.
     *
     * @param paymentSchedule1 credit payment schedule 1.
     * @param paymentSchedule2 credit payment schedule 2.
     * @return true if given arguments are equal, false otherwise.
     */
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
            ) {
                log.error("Payment schedule element\n" + paymentSchedule1.get(i) +
                        "\n is not equal to \n" + paymentSchedule2.get(i));
                return false;
            }
        }

        return true;
    }
}