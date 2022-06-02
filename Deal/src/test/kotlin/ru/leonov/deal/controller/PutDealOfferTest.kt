package ru.leonov.deal.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.leonov.deal.exceptions.ApplicationException
import ru.leonov.deal.service.ApplyOfferService
import ru.leonov.deal.service.CreditCalculationService
import ru.leonov.deal.service.GetOffersService
import ru.leonov.deal.test_data.LoanOfferTestData.Companion.fineRequestJSON
import ru.leonov.deal.test_data.LoanOfferTestData.Companion.fineRequestObject
import ru.leonov.deal.test_data.LoanOfferTestData.Companion.noApplicationIdRequestJSON
import ru.leonov.deal.test_data.LoanOfferTestData.Companion.noInsuranceRequestJSON
import ru.leonov.deal.test_data.LoanOfferTestData.Companion.noMPaymentRequestJSON
import ru.leonov.deal.test_data.LoanOfferTestData.Companion.noRAmountRequestJSON
import ru.leonov.deal.test_data.LoanOfferTestData.Companion.noRateRequestJSON
import ru.leonov.deal.test_data.LoanOfferTestData.Companion.noSalaryRequestJSON
import ru.leonov.deal.test_data.LoanOfferTestData.Companion.noTAmountRequestJSON
import ru.leonov.deal.test_data.LoanOfferTestData.Companion.noTermRequestJSON

@WebMvcTest
class PutDealOfferTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var getOffersService: GetOffersService

    @MockkBean
    lateinit var applyOfferService: ApplyOfferService

    @MockkBean
    lateinit var creditCalculationService: CreditCalculationService

    //testing that known request will produce known response
    @Test
    fun putDealOfferTest() {

        justRun { applyOfferService.applyOffer(fineRequestObject()) }

        mockMvc.perform(
            put("/deal/offer")
                .content(fineRequestJSON())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)

        verify(exactly = 1) { applyOfferService.applyOffer(fineRequestObject()) }

    }

    //test if requested application id not found in database
    @Test
    fun putDealOfferWhenApplicationNotFoundTest() {

        every { applyOfferService.applyOffer(fineRequestObject()) } throws
                ApplicationException(ApplicationException.ExceptionCause.APPLICATION_NOT_FOUND)

        mockMvc.perform(
            put("/deal/offer")
                .content(fineRequestJSON())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)

        verify(exactly = 1) { applyOfferService.applyOffer(fineRequestObject()) }
    }


    //testing that requests without one of required parameters will produce code 400 response
    @Test
    fun performTestsForMissingParams() {

        paramIssuesTest(noApplicationIdRequestJSON())
        paramIssuesTest(noRAmountRequestJSON())
        paramIssuesTest(noTAmountRequestJSON())
        paramIssuesTest(noTermRequestJSON())
        paramIssuesTest(noMPaymentRequestJSON())
        paramIssuesTest(noRateRequestJSON())
        paramIssuesTest(noInsuranceRequestJSON())
        paramIssuesTest(noSalaryRequestJSON())

        verify(exactly = 0) { getOffersService.getOffers(any()) }
    }

    private fun paramIssuesTest(noParamJson: String) {

        mockMvc.perform(
            put("/deal/offer")
                .content(noParamJson)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)

    }
}