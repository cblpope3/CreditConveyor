package ru.leonov.deal.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.util.AssertionErrors.assertEquals
import ru.leonov.deal.client.ConveyorAppClient
import ru.leonov.deal.repository.ApplicationRepository
import ru.leonov.deal.repository.ClientRepository
import ru.leonov.deal.test_data.LoanOfferServiceTestData.Companion.fineApplicationEntityNoId
import ru.leonov.deal.test_data.LoanOfferServiceTestData.Companion.fineApplicationEntityWithId
import ru.leonov.deal.test_data.LoanOfferServiceTestData.Companion.fineClientEntityNoId
import ru.leonov.deal.test_data.LoanOfferServiceTestData.Companion.fineClientEntityWithId
import ru.leonov.deal.test_data.LoanOfferServiceTestData.Companion.fineRequestObject
import ru.leonov.deal.test_data.LoanOfferServiceTestData.Companion.orderedResponseWithCorrectIdObject
import ru.leonov.deal.test_data.LoanOfferServiceTestData.Companion.unorderedResponseWithWrongIdObject

@SpringBootTest
@ActiveProfiles("test")
class GetOffersServiceTest {

    @MockkBean
    lateinit var conveyorAppClient: ConveyorAppClient

    @MockkBean
    lateinit var clientRepository: ClientRepository

    @MockkBean
    lateinit var applicationRepository: ApplicationRepository

    @Autowired
    lateinit var getOffersService: GetOffersService

    @Test
    fun fineGetOffersJob() {
        // mocking client repository call
        every { clientRepository.save(fineClientEntityNoId()) } returns
                fineClientEntityWithId()

        // mocking application repository call
        every { applicationRepository.save(fineApplicationEntityNoId()) } returns
                fineApplicationEntityWithId()

        // mocking conveyor app client call
        // client must return unordered list of LoanOfferDTO with wrong application id
        every { conveyorAppClient.requestLoanOffer(fineRequestObject()) } returns
                unorderedResponseWithWrongIdObject()

        // performing GetOffersService request and comparing response to expected
        val expectedResponse = orderedResponseWithCorrectIdObject()
        val actualResponse = getOffersService.getOffers(fineRequestObject())

        assertEquals("Response must be correct and ordered.", expectedResponse, actualResponse)

        // verifying mock calls number
        verify(exactly = 1) { clientRepository.save(any()) }
        verify(exactly = 1) { applicationRepository.save(any()) }
        verify(exactly = 1) { conveyorAppClient.requestLoanOffer(any()) }

    }
}