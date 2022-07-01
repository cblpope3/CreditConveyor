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
import ru.leonov.deal.exception.ApplicationException
import ru.leonov.deal.service.ApplyOfferService
import ru.leonov.deal.service.CreditCalculationService
import ru.leonov.deal.service.GetOffersService
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.fineRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.fineRequestObject
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noAccountRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noDependentAmountRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noEINNRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noEPositionRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noESalaryRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noEStatusRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noEWECurrentRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noEWETotalRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noEmploymentRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noGenderRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noMStatusRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noPIBranchRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.noPIDateRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.wrongAccountRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.wrongEPositionRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.wrongEStatusRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.wrongGenderRequestJSON
import ru.leonov.deal.test_data.FinishRegistrationRequestTestData.Companion.wrongMStatusRequestJSON


@WebMvcTest
class PutDealCalculationTest {

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
    fun putDealCalculationTest() {

        justRun { creditCalculationService.calculateCredit(fineRequestObject(), 111) }

        mockMvc.perform(
            put("/deal/calculate/111")
                .content(fineRequestJSON())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)

        verify(exactly = 1) { creditCalculationService.calculateCredit(any(), any()) }

    }

    //test if requested application id not found in database
    @Test
    fun putDealCalculationWhenApplicationNotFoundTest() {

        every { creditCalculationService.calculateCredit(fineRequestObject(), 111) } throws
                ApplicationException(ApplicationException.ExceptionCause.APPLICATION_NOT_FOUND)

        mockMvc.perform(
            put("/deal/calculate/111")
                .content(fineRequestJSON())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)

        verify(exactly = 1) { creditCalculationService.calculateCredit(any(), any()) }
    }

    //testing that requests without one of required parameters will produce code 400 response
    @Test
    fun performTestsForMissingParams() {

        paramLackTest(noGenderRequestJSON())
        paramLackTest(noMStatusRequestJSON())
        paramLackTest(noDependentAmountRequestJSON())
        paramLackTest(noPIDateRequestJSON())
        paramLackTest(noPIBranchRequestJSON())
        paramLackTest(noEmploymentRequestJSON())
        paramLackTest(noEStatusRequestJSON())
        paramLackTest(noEINNRequestJSON())
        paramLackTest(noESalaryRequestJSON())
        paramLackTest(noEPositionRequestJSON())
        paramLackTest(noEWETotalRequestJSON())
        paramLackTest(noEWECurrentRequestJSON())
        paramLackTest(noAccountRequestJSON())

        verify(exactly = 0) { getOffersService.getOffers(any()) }
    }

    private fun paramLackTest(noParamJson: String) {

        mockMvc.perform(
            put("/deal/calculate/111")
                .content(noParamJson)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)

    }

    //testing that requests without one of required parameters will produce code 400 response
    @Test
    fun performTestsForWrongFormatParams() {

        paramWrongFormatTest(wrongGenderRequestJSON())
        paramWrongFormatTest(wrongMStatusRequestJSON())
        paramWrongFormatTest(wrongEStatusRequestJSON())
        paramWrongFormatTest(wrongEPositionRequestJSON())
        paramWrongFormatTest(wrongAccountRequestJSON())

        verify(exactly = 0) { getOffersService.getOffers(any()) }
    }

    private fun paramWrongFormatTest(noParamJson: String) {

        mockMvc.perform(
            put("/deal/calculate/111")
                .content(noParamJson)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)

    }
}