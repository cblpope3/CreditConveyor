package ru.leonov.deal.test_data

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.leonov.deal.dto.LoanApplicationRequestDTO
import ru.leonov.deal.dto.LoanOfferDTO
import ru.leonov.deal.mapper.loanRequestToClient
import ru.leonov.deal.model.entity.ApplicationEntity
import ru.leonov.deal.model.entity.ClientEntity
import ru.leonov.deal.model.enums.ApplicationStatusEnum
import ru.leonov.deal.model.record.ApplicationHistoryElementRecord
import java.time.LocalDate

class LoanOfferServiceTestData {

    companion object {

//        private const val loanRequestMapper = LoanRequestMapper

        private const val clientId: Long = 112
        private const val applicationId: Long = 221

        private fun fineRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun fineRequestObject(): LoanApplicationRequestDTO {
            val mapper = jacksonObjectMapper()
            mapper.registerModule(JavaTimeModule())

            return mapper.readValue(fineRequestJSON())
        }

        fun fineClientEntityNoId(): ClientEntity {
            return loanRequestToClient(fineRequestObject())
        }

        fun fineClientEntityWithId(): ClientEntity {
            val resultClient = fineClientEntityNoId()
            resultClient.id = clientId
            return resultClient
        }

        fun fineApplicationEntityNoId(): ApplicationEntity {
            val applicationStatus = ApplicationStatusEnum.PREAPPROVAL
            val resultClient = fineClientEntityWithId()

            val applicationStatusHistoryElement = ApplicationHistoryElementRecord(
                date = LocalDate.now(),
                status = applicationStatus
            )

            val applicationStatusHistory = ArrayList(listOf(applicationStatusHistoryElement))

            return ApplicationEntity(
                client = resultClient,
                status = applicationStatus,
                statusHistory = applicationStatusHistory,
                creationDate = LocalDate.now()
            )
        }

        fun fineApplicationEntityWithId(): ApplicationEntity {
            val application = fineApplicationEntityNoId()
            application.id = applicationId
            return application
        }

        private fun orderedResponseWithWrongIdJSON(): String {
            return """[
                    {
                        "applicationId": 30,
                        "requestedAmount": 100000.00,
                        "totalAmount": 107003.10,
                        "term": 10,
                        "monthlyPayment": 10700.31,
                        "rate": 15.0,
                        "isInsuranceEnabled": false,
                        "isSalaryClient": false
                    },
                    {
                        "applicationId": 30,
                        "requestedAmount": 100000.00,
                        "totalAmount": 206528.30,
                        "term": 10,
                        "monthlyPayment": 10652.83,
                        "rate": 14.0,
                        "isInsuranceEnabled": true,
                        "isSalaryClient": false
                    },
                    {
                        "applicationId": 30,
                        "requestedAmount": 100000.00,
                        "totalAmount": 105582.10,
                        "term": 10,
                        "monthlyPayment": 10558.21,
                        "rate": 12.0,
                        "isInsuranceEnabled": false,
                        "isSalaryClient": true
                    },
                    {
                        "applicationId": 30,
                        "requestedAmount": 100000.00,
                        "totalAmount": 205110.70,
                        "term": 10,
                        "monthlyPayment": 10511.07,
                        "rate": 11.0,
                        "isInsuranceEnabled": true,
                        "isSalaryClient": true
                    }
                ]"""
        }

        fun unorderedResponseWithWrongIdObject(): MutableList<LoanOfferDTO> {
            val mapper = jacksonObjectMapper()
            val resultList: ArrayList<LoanOfferDTO> = mapper.readValue(orderedResponseWithWrongIdJSON())
            resultList.add(resultList[0])
            resultList.removeAt(0)
            return resultList
        }

        fun orderedResponseWithCorrectIdObject(): List<LoanOfferDTO> {
            val mapper = jacksonObjectMapper()
            val orderedList: ArrayList<LoanOfferDTO> = mapper.readValue(orderedResponseWithWrongIdJSON())

            orderedList.forEach { responseElement -> responseElement.applicationId = applicationId }
            return orderedList
        }
    }
}