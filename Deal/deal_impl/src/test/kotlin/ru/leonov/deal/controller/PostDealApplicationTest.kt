package ru.leonov.deal.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.leonov.deal.service.ApplyOfferService
import ru.leonov.deal.service.CreditCalculationService
import ru.leonov.deal.service.GetOffersService
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.fineRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.fineRequestObject
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.fineResponseJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.fineResponseObject
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.noAmountRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.noBirthDateRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.noEmailRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.noFNameRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.noLNameRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.noMNameRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.noMNameRequestObject
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.noPNumberRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.noPSeriesRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.noTermRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.wrongAmountRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.wrongBirthDateRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.wrongEmailRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.wrongFNameRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.wrongLNameRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.wrongMNameRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.wrongPNumberRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.wrongPSeriesRequestJSON
import ru.leonov.deal.test_data.LoanRequestTestData.Companion.wrongTermRequestJSON


@WebMvcTest
class PostDealApplicationTest {

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
    fun postDealTest() {

        every { getOffersService.getOffers(fineRequestObject()) } returns
                fineResponseObject()

        mockMvc.perform(
            post("/deal/application")
                .content(fineRequestJSON())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(fineResponseJSON()))

        verify(exactly = 1) { getOffersService.getOffers(fineRequestObject()) }

    }

    //testing that known request without middle name will produce known response
    @Test
    fun noMNameTest() {

        every { getOffersService.getOffers(noMNameRequestObject()) } returns
                fineResponseObject()

        mockMvc.perform(
            post("/deal/application")
                .content(noMNameRequestJSON())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().json(fineResponseJSON()))

        //request with wrong middle name will produce response with code 400
        mockMvc.perform(
            post("/deal/application")
                .content(wrongMNameRequestJSON())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)

        verify(exactly = 1) { getOffersService.getOffers(noMNameRequestObject()) }

    }

    //testing that requests without one of required parameters will produce code 400 response
    //also, requests with wrong parameters format will produce code 400 response too.
    @Test
    fun performTestsForWrongOrMissingParams() {

        paramIssuesTest(noAmountRequestJSON(), wrongAmountRequestJSON())
        paramIssuesTest(noTermRequestJSON(), wrongTermRequestJSON())
        paramIssuesTest(noFNameRequestJSON(), wrongFNameRequestJSON())
        paramIssuesTest(noLNameRequestJSON(), wrongLNameRequestJSON())
        paramIssuesTest(noEmailRequestJSON(), wrongEmailRequestJSON())
        paramIssuesTest(noBirthDateRequestJSON(), wrongBirthDateRequestJSON())
        paramIssuesTest(noPSeriesRequestJSON(), wrongPSeriesRequestJSON())
        paramIssuesTest(noPNumberRequestJSON(), wrongPNumberRequestJSON())

        verify(exactly = 0) { getOffersService.getOffers(any()) }
    }

    private fun paramIssuesTest(noParamJson: String, wrongParamJson: String) {

        mockMvc.perform(
            post("/deal/application")
                .content(noParamJson)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)

        mockMvc.perform(
            post("/deal/application")
                .content(wrongParamJson)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)

    }
}