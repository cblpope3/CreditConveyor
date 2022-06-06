package ru.leonov.deal.service

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.leonov.deal.client.ConveyorAppClient
import ru.leonov.deal.repository.ApplicationRepository
import ru.leonov.deal.repository.ClientRepository
import ru.leonov.deal.repository.CreditRepository
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.applicationEntityStoredInDBBefore
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.applicationEntityUpdatedAfterCreditCalculation
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.applicationOptionalStoredInDBBefore
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.completedClientEntity
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.creditEntityExpectedToSaveInDBWithoutIdObject
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.creditEntityExpectedToSaveInDBWithIdObject
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.finishRegistrationRequestObject
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.scoringDataRequestObject
import ru.leonov.deal.test_data.CreditCalculationTestData.Companion.scoringDataResponseObject

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


        // performing ApplyOfferService request and comparing thrown exception to expected
        creditCalculationService.calculateCredit(finishRegistrationRequestObject(), applicationEntityStoredInDBBefore().id)


        // verifying mocked repository calls
        verify(exactly = 1) { applicationRepository.findById(any()) }
        verify(exactly = 1) { applicationRepository.save(any()) }
        verify(exactly = 1) { creditRepository.save(any()) }
        verify(exactly = 1) { clientRepository.save(any()) }
        verify(exactly = 1) { conveyorAppClient.requestCreditCalculation(any()) }

    }

    //todo test if application is not found in database
    @Test
    fun creditCalculationIfApplicationNotFoundTest() {

    }

    //todo test if application had been denied by credit conveyor
    @Test
    fun creditCalculationIfApplicationIsDeniedByConveyorTest() {

    }

    //todo test if application had been denied previously
    @Test
    fun creditCalculationIfApplicationIsArchivedTest() {

    }
}