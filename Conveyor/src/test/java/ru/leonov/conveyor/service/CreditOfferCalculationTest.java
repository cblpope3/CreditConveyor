package ru.leonov.conveyor.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.leonov.conveyor.dto.ModelsLoanOfferDTO;
import ru.leonov.conveyor.test_data.LoanOfferTestData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class CreditOfferCalculationTest {

    private final BigDecimal baseRate = BigDecimal.valueOf(15);
    private CreditCalculationService creditCalculationService;

    @BeforeEach
    void SetUp() {
        creditCalculationService = new CreditCalculationService();
    }

    //testing credit offers generation
    @Test
    void generateCreditOffers() {
        //simulating service method call
        List<ModelsLoanOfferDTO> generatedOffers = creditCalculationService.generateCreditOffers(
                LoanOfferTestData.getFineLoanOfferRequest().getAmount(),
                LoanOfferTestData.getFineLoanOfferRequest().getTerm(),
                baseRate);

        System.out.println(generatedOffers.stream()
                .map(offer ->
                        offer.getTotalAmount().setScale(2, RoundingMode.HALF_UP)
                )
                .collect(Collectors.toList()));


        //checking that response is correct
        assertThat(generatedOffers)
                .hasSameSizeAs(LoanOfferTestData.getFineLoanOfferResponse());

        assertTrue(compareLoanOffers(generatedOffers.get(0), LoanOfferTestData.getFineLoanOfferResponse().get(0)));
        assertTrue(compareLoanOffers(generatedOffers.get(1), LoanOfferTestData.getFineLoanOfferResponse().get(1)));
        assertTrue(compareLoanOffers(generatedOffers.get(2), LoanOfferTestData.getFineLoanOfferResponse().get(2)));
        assertTrue(compareLoanOffers(generatedOffers.get(3), LoanOfferTestData.getFineLoanOfferResponse().get(3)));
    }

    private boolean compareLoanOffers(ModelsLoanOfferDTO offer1, ModelsLoanOfferDTO offer2) {
        //since applicationId field is randomly generated, we ignore this field in the assertion
        return
                offer1.getRequestedAmount().compareTo(offer2.getRequestedAmount()) == 0 &&
                        offer1.getTotalAmount().compareTo(offer2.getTotalAmount()) == 0 &&
                        offer1.getMonthlyPayment().compareTo(offer2.getMonthlyPayment()) == 0 &&
                        offer1.getRate().compareTo(offer2.getRate()) == 0 &&
                        offer1.getTerm().equals(offer2.getTerm()) &&
                        offer1.getIsInsuranceEnabled().equals(offer2.getIsInsuranceEnabled()) &&
                        offer1.getIsSalaryClient().equals(offer2.getIsSalaryClient());

    }
}