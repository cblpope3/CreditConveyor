package ru.leonov.deal.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.util.AssertionErrors.assertEquals
import ru.leonov.deal.exception.ApplicationException
import ru.leonov.deal.repository.ApplicationRepository
import ru.leonov.deal.test_data.ApplyOfferServiceTestData.Companion.emptyApplicationResult
import ru.leonov.deal.test_data.ApplyOfferServiceTestData.Companion.filledApplicationEntity
import ru.leonov.deal.test_data.ApplyOfferServiceTestData.Companion.fineRequestObject
import ru.leonov.deal.test_data.ApplyOfferServiceTestData.Companion.optionalUnfilledApplicationEntity

@SpringBootTest
@ActiveProfiles("test")
class ApplyOfferServiceTest {

    @MockkBean
    lateinit var applicationRepository: ApplicationRepository

    @Autowired
    lateinit var applyOfferService: ApplyOfferService

    //testing fine apply application call
    @Test
    fun calculateCreditTest() {

        // mocking application repository findById call
        every { applicationRepository.findById(fineRequestObject().applicationId) } returns
                optionalUnfilledApplicationEntity()

        // mocking application repository save call
        every { applicationRepository.save(filledApplicationEntity()) } returns
                filledApplicationEntity()

        applyOfferService.applyOffer(fineRequestObject())

        // verifying mocked repository calls
        verify(exactly = 1) { applicationRepository.findById(fineRequestObject().applicationId) }
        verify(exactly = 1) { applicationRepository.findById(any()) }
        verify(exactly = 1) { applicationRepository.save(filledApplicationEntity()) }
        verify(exactly = 1) { applicationRepository.save(any()) }
    }

    // testing if requested application is not found
    @Test
    fun calculateCreditIfApplicationIsNotFoundTest() {

        // mocking application repository findById call
        every { applicationRepository.findById(fineRequestObject().applicationId) } returns
                emptyApplicationResult()

        // mocking application repository save call
        every { applicationRepository.save(filledApplicationEntity()) } returns
                filledApplicationEntity()

        // performing ApplyOfferService request and comparing thrown exception to expected
        val expectedException = ApplicationException(ApplicationException.ExceptionCause.APPLICATION_NOT_FOUND)
        val thrownException = assertThrows<ApplicationException> { applyOfferService.applyOffer(fineRequestObject()) }

        assertEquals("Thrown exception must be equal to expected", expectedException, thrownException)

        // verifying mocked repository calls
        verify(exactly = 1) { applicationRepository.findById(fineRequestObject().applicationId) }
        verify(exactly = 1) { applicationRepository.findById(any()) }
        verify(exactly = 0) { applicationRepository.save(any()) }
    }
}