package ru.leonov.deal.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.AssertionErrors.assertEquals
import ru.leonov.deal.client.ConveyorAppClient
import ru.leonov.deal.exception.ApplicationException
import ru.leonov.deal.repository.ApplicationRepository
import ru.leonov.deal.repository.ClientRepository
import ru.leonov.deal.repository.CreditRepository
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.applicationEntityStoredInDBBefore
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.applicationEntityUpdatedAfterCreditCalculation
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.applicationEntityUpdatedAfterCreditRejected
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.applicationOptionalStoredInDBBefore
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.applicationOptionalStoredInDBBeforeWithCCDeniedStatus
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.applicationOptionalStoredInDBBeforeWithClientDeniedStatus
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.completedClientEntity
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.creditEntityExpectedToSaveInDBWithIdObject
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.creditEntityExpectedToSaveInDBWithoutIdObject
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.finishRegistrationRequestObject
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.scoringDataRequestObject
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.scoringDataResponseObject
import java.util.*

@SpringBootTest
class CreditCalculationServiceTest {

    @MockkBean
    lateinit var clientRepository: ClientRepository

    @MockkBean
    lateinit var creditRepository: CreditRepository

    @MockkBean
    lateinit var applicationRepository: ApplicationRepository

    @MockkBean
    lateinit var conveyorAppClient: ConveyorAppClient

    @Autowired
    lateinit var creditCalculationService: CreditCalculationService

    //testing fine finish registration call
    @Test
    fun creditCalculationServiceTest() {
        // mocking application repository findById call
        every { applicationRepository.findById(applicationEntityStoredInDBBefore().id) } returns
                applicationOptionalStoredInDBBefore()

        // mocking client repository save call
        every { clientRepository.save(completedClientEntity()) } returns
                completedClientEntity()

        // mocking conveyor app client call
        every { conveyorAppClient.requestCreditCalculation(scoringDataRequestObject()) } returns
                scoringDataResponseObject()

        // mocking credit repository save call
        every { creditRepository.save(creditEntityExpectedToSaveInDBWithoutIdObject()) } returns
                creditEntityExpectedToSaveInDBWithIdObject()

        // mocking application repository save call
        every { applicationRepository.save(applicationEntityUpdatedAfterCreditCalculation()) } returns
                applicationEntityUpdatedAfterCreditCalculation()


        // performing ApplyOfferService request
        creditCalculationService.calculateCredit(
            finishRegistrationRequestObject(),
            //fixme deal with nullable values
            applicationEntityStoredInDBBefore().id!!
        )


        // verifying mocked repository calls
        verify(exactly = 1) { applicationRepository.findById(any()) }
        verify(exactly = 1) { applicationRepository.save(any()) }
        verify(exactly = 1) { creditRepository.save(any()) }
        verify(exactly = 1) { clientRepository.save(any()) }
        verify(exactly = 1) { conveyorAppClient.requestCreditCalculation(any()) }

    }

    // test if application is not found in database
    @Test
    fun creditCalculationIfApplicationNotFoundTest() {
        // mocking application repository findById call
        every { applicationRepository.findById(applicationEntityStoredInDBBefore().id) } returns
                Optional.empty()

        // performing ApplyOfferService request and comparing thrown exception to expected
        val expectedException = ApplicationException(ApplicationException.ExceptionCause.APPLICATION_NOT_FOUND)
        val thrownException = assertThrows<ApplicationException> {
            creditCalculationService.calculateCredit(
                finishRegistrationRequestObject(),
                //fixme deal with nullable values
                applicationEntityStoredInDBBefore().id!!
            )
        }

        assertEquals("Thrown wrong exception.", expectedException, thrownException)

        // verifying mocked repository calls
        verify(exactly = 1) { applicationRepository.findById(any()) }
        verify(exactly = 0) { applicationRepository.save(any()) }
        verify(exactly = 0) { creditRepository.save(any()) }
        verify(exactly = 0) { clientRepository.save(any()) }
        verify(exactly = 0) { conveyorAppClient.requestCreditCalculation(any()) }
    }

    // test if application had been denied previously
    @Test
    fun creditCalculationIfApplicationIsArchivedTest() {
        // mocking application repository findById call
        every { applicationRepository.findById(1) } returns
                applicationOptionalStoredInDBBeforeWithCCDeniedStatus()
        every { applicationRepository.findById(2) } returns
                applicationOptionalStoredInDBBeforeWithClientDeniedStatus()

        // performing ApplyOfferService request
        creditCalculationService.calculateCredit(finishRegistrationRequestObject(), 1)
        creditCalculationService.calculateCredit(finishRegistrationRequestObject(), 2)

        // verifying mocked repository calls
        verify(exactly = 2) { applicationRepository.findById(any()) }
        verify(exactly = 0) { applicationRepository.save(any()) }
        verify(exactly = 0) { creditRepository.save(any()) }
        verify(exactly = 0) { clientRepository.save(any()) }
        verify(exactly = 0) { conveyorAppClient.requestCreditCalculation(any()) }
    }

    // test if application had been denied by credit conveyor
    @Test
    fun creditCalculationIfApplicationIsDeniedByConveyorTest() {
        // mocking application repository findById call
        every { applicationRepository.findById(applicationEntityStoredInDBBefore().id) } returns
                applicationOptionalStoredInDBBefore()

        // mocking client repository save call
        every { clientRepository.save(completedClientEntity()) } returns
                completedClientEntity()

        // mocking conveyor app client call
        every { conveyorAppClient.requestCreditCalculation(scoringDataRequestObject()) } returns
                null

        // mocking application repository save call
        every { applicationRepository.save(applicationEntityUpdatedAfterCreditRejected()) } returns
                applicationEntityUpdatedAfterCreditRejected()


        // performing ApplyOfferService request
        creditCalculationService.calculateCredit(
            finishRegistrationRequestObject(),
            //fixme deal with nullable values
            applicationEntityStoredInDBBefore().id!!
        )


        // verifying mocked repository calls
        verify(exactly = 1) { applicationRepository.findById(any()) }
        verify(exactly = 1) { applicationRepository.save(any()) }
        verify(exactly = 0) { creditRepository.save(any()) }
        verify(exactly = 1) { clientRepository.save(any()) }
        verify(exactly = 1) { conveyorAppClient.requestCreditCalculation(any()) }
    }

}