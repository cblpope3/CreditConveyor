package ru.leonov.deal.test_data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.leonov.deal.dto.LoanOfferDTO
import ru.leonov.deal.model.entity.ApplicationEntity
import ru.leonov.deal.model.entity.ClientEntity
import ru.leonov.deal.model.record.ApplicationHistoryElementRecord
import java.time.LocalDate
import java.util.*

class ApplyOfferServiceTestData {
    companion object {

        private const val applicationId: Long = 221

        private fun fineRequestJSON(): String {
            return """
                    {
                        "applicationId": 331,
                        "requestedAmount": 643445.43,
                        "totalAmount": 669439.33,
                        "term": 7,
                        "monthlyPayment": 95634.19,
                        "rate": 12.0,
                        "isInsuranceEnabled": false,
                        "isSalaryClient": true
                    }
            """.trimIndent()
        }

        fun fineRequestObject(): LoanOfferDTO {
            val mapper = jacksonObjectMapper()
            val loanOffer: LoanOfferDTO = mapper.readValue(fineRequestJSON())
            loanOffer.applicationId = applicationId
            return loanOffer
        }

        private fun fineClientEntity(): ClientEntity {
            //taking ready client entity data from LoanOfferServiceTestData class because this data is not important
            return LoanOfferServiceTestData.fineClientEntityWithId()
        }

        private fun unfilledApplicationEntity(): ApplicationEntity {
            val applicationStatus = ApplicationEntity.Status.PREAPPROVAL
            val resultClient = fineClientEntity()

            val applicationStatusHistoryElement = ApplicationHistoryElementRecord()
            applicationStatusHistoryElement.date = LocalDate.now().minusDays(2)
            applicationStatusHistoryElement.status = applicationStatus
            val applicationStatusHistory = arrayListOf(applicationStatusHistoryElement)

            val resultApplication = ApplicationEntity()

            resultApplication.id = applicationId
            resultApplication.client = resultClient
            resultApplication.status = applicationStatus
            resultApplication.statusHistory = applicationStatusHistory
            resultApplication.creationDate = LocalDate.now().minusDays(2)

            return resultApplication
        }

        fun optionalUnfilledApplicationEntity(): Optional<ApplicationEntity> {
            return Optional.of(unfilledApplicationEntity())
        }

        fun filledApplicationEntity(): ApplicationEntity {
            val application = unfilledApplicationEntity()

            // update application status to approved
            val applicationStatus = ApplicationEntity.Status.APPROVED

            val applicationStatusHistoryElement = ApplicationHistoryElementRecord()
            applicationStatusHistoryElement.date = LocalDate.now()
            applicationStatusHistoryElement.status = applicationStatus

            application.status = applicationStatus
            application.statusHistory.add(applicationStatusHistoryElement)

            // set applied offer to application
            application.appliedOffer = fineRequestObject()

            return application
        }

        fun emptyApplicationResult(): Optional<ApplicationEntity> {
            return Optional.empty()
        }

    }
}