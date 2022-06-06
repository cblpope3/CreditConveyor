package ru.leonov.deal.test_data

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.leonov.deal.dto.CreditDTO
import ru.leonov.deal.dto.FinishRegistrationRequestDTO
import ru.leonov.deal.dto.ScoringDataDTO
import ru.leonov.deal.model.entity.ApplicationEntity
import ru.leonov.deal.model.entity.ClientEntity
import ru.leonov.deal.model.entity.CreditEntity
import ru.leonov.deal.model.record.ApplicationHistoryElementRecord
import ru.leonov.deal.model.record.EmploymentRecord
import java.time.LocalDate
import java.util.*

class CreditCalculationTestData {
    companion object {

        /**
         * This object stores previous requests history
         */
        private object PreviouslyStoredData {

            fun applicationsDBDataBeforeCallJSON(): String {
                return """
                    {
                     "id": 31,
                     "status": "APPROVED",
                     "creationDate": "2022-06-03",
                     "appliedOffer": 
                         {
                             "rate": 11.0, 
                             "term": 10, 
                             "totalAmount": 205110.70, 
                             "applicationId": 31, 
                             "isSalaryClient": true, 
                             "monthlyPayment": 10511.07, 
                             "requestedAmount": 100000.00, 
                             "isInsuranceEnabled": true
                         },
                     "statusHistory": 
                        [
                            {
                            "date": [2022, 6, 3], 
                            "status": "PREAPPROVAL"
                            }, 
                            {
                            "date": [2022, 6, 3], 
                            "status": "APPROVED"
                            }
                        ]
                     }

                """.trimIndent()

            }

            fun clientDBDataBeforeCallJSON(): String {
                return """
                    {
                        "id": 74,
                        "lastName": "Pupkin",
                        "firstName": "Vasiliy",
                        "middleName": "Ulukbekovich",
                        "birthDate": "1998-04-28",
                        "email": "vasyapoop@ulukbek.ru",
                        "passport": 
                            {
                                "number": 567321, 
                                "series": 2467, 
                                "issueDate": null, 
                                "issueBranch": null
                            }                         
                    }
                """.trimIndent()
            }

        }

        //+++++++++++++++++++++++++++++
        // CLIENT ENTITY
        //+++++++++++++++++++++++++++++

        private fun clientEntityStoredInDBBefore(): ClientEntity {
            val mapper = jacksonObjectMapper()
            mapper.registerModule(JavaTimeModule())

            return mapper.readValue(PreviouslyStoredData.clientDBDataBeforeCallJSON())
        }

        fun completedClientEntity(): ClientEntity {
            val clientEntity = applicationEntityStoredInDBBefore().client

            val passportRecord = clientEntity.passport
            passportRecord.issueDate = finishRegistrationRequestObject().passportIssueDate
            passportRecord.issueBranch = finishRegistrationRequestObject().passportIssueBranch

            val employmentDTO = finishRegistrationRequestObject().employment
            val employmentRecord = EmploymentRecord()
            employmentRecord.employmentStatus = EmploymentRecord.EmploymentStatus.valueOf(
                employmentDTO.employmentStatus.value
            )
            employmentRecord.employer = employmentDTO.employerINN
            employmentRecord.salary = employmentDTO.salary
            employmentRecord.position = EmploymentRecord.Position.valueOf(employmentDTO.position.value)
            employmentRecord.workExperienceTotal = employmentDTO.workExperienceTotal
            employmentRecord.workExperienceCurrent = employmentDTO.workExperienceCurrent


            clientEntity.gender = ClientEntity.Gender.valueOf(finishRegistrationRequestObject().gender.value)
            clientEntity.maritalStatus =
                ClientEntity.MartialStatus.valueOf(finishRegistrationRequestObject().maritalStatus.value)
            clientEntity.dependentAmount = finishRegistrationRequestObject().dependentAmount
            clientEntity.account = finishRegistrationRequestObject().account.toLong()

            clientEntity.passport = passportRecord
            clientEntity.employment = employmentRecord

            return clientEntity

        }

        //+++++++++++++++++++++++++++++
        // APPLICATION
        //+++++++++++++++++++++++++++++

        fun applicationEntityStoredInDBBefore(): ApplicationEntity {
            val mapper = jacksonObjectMapper()
            mapper.registerModule(JavaTimeModule())

            val applicationEntity: ApplicationEntity =
                mapper.readValue(PreviouslyStoredData.applicationsDBDataBeforeCallJSON())
            applicationEntity.client = clientEntityStoredInDBBefore()
            return applicationEntity
        }

        fun applicationOptionalStoredInDBBefore(): Optional<ApplicationEntity> {
            return Optional.of(applicationEntityStoredInDBBefore())
        }

        fun applicationEntityUpdatedAfterCreditCalculation(): ApplicationEntity {
            val application = applicationEntityStoredInDBBefore()
            application.client = completedClientEntity()
            application.credit = creditEntityExpectedToSaveInDBWithIdObject()

            val newApplicationStatus = ApplicationEntity.Status.CC_APPROVED

            val applicationStatusHistoryElement = ApplicationHistoryElementRecord()
            applicationStatusHistoryElement.date = LocalDate.now()
            applicationStatusHistoryElement.status = newApplicationStatus

            application.status = newApplicationStatus
            application.statusHistory.add(applicationStatusHistoryElement)

            return application
        }

        //+++++++++++++++++++++++++++++
        // FINISH REGISTRATION DTO
        //+++++++++++++++++++++++++++++

        private fun finishRegistrationRequestJSON(): String {
            return """
                {
                  "gender": "MALE",
                  "maritalStatus": "SINGLE",
                  "dependentAmount": 0,
                  "passportIssueDate": "2013-05-22",
                  "passportIssueBranch": "УВД №66 г. Бобруйска.",
                  "employment": {
                    "employmentStatus": "EMPLOYED",
                    "employerINN": "43646375637",
                    "salary": 5501,
                    "position": "WORKER",
                    "workExperienceTotal": 21,
                    "workExperienceCurrent": 21
                  },
                  "account": "234234264363"
                }
            """.trimIndent()
        }

        fun finishRegistrationRequestObject(): FinishRegistrationRequestDTO {
            val mapper = jacksonObjectMapper()
            mapper.registerModule(JavaTimeModule())

            return mapper.readValue(finishRegistrationRequestJSON())
        }

        //+++++++++++++++++++++++++++++
        // SCORING DATA
        //+++++++++++++++++++++++++++++

        private fun scoringDataRequestJSON(): String {
            return """
                {
                    "amount": 100000.00,
                    "term": 10,
                    "firstName": "Vasiliy",
                    "lastName": "Pupkin",
                    "middleName": "Ulukbekovich",
                    "gender": "MALE",
                    "birthdate": "1998-04-28",
                    "passportSeries": "2467",
                    "passportNumber": "567321",
                    "passportIssueDate": "2013-05-22",
                    "passportIssueBranch": "УВД №66 г. Бобруйска.",
                    "maritalStatus": "SINGLE",
                    "dependentAmount": 0,
                    "employment": {
                        "employmentStatus": "EMPLOYED",
                        "employerINN": "43646375637",
                        "salary": 5501,
                        "position": "WORKER",
                        "workExperienceTotal": 21,
                        "workExperienceCurrent": 21
                    },
                    "account": 234234264363,
                    "isInsuranceEnabled": true,
                    "isSalaryClient": true
                }
            """.trimIndent()

        }

        fun scoringDataRequestObject(): ScoringDataDTO {
            val mapper = jacksonObjectMapper()
            mapper.registerModule(JavaTimeModule())

            return mapper.readValue(scoringDataRequestJSON())
        }

        private fun scoringDataResponseJSON(): String {
            return """
                {
                    "amount": 100000.00,
                    "term": 10,
                    "monthlyPayment": 10700.31,
                    "rate": 15.0,
                    "psk": 15.72,
                    "isInsuranceEnabled": true,
                    "isSalaryClient": true,
                    "paymentSchedule": [
                        {
                            "number": 1,
                            "date": "2022-07-03",
                            "totalPayment": 10700.31,
                            "interestPayment": 1250.00,
                            "debtPayment": 9450.31,
                            "remainingDebt": 90549.69
                        },
                        {
                            "number": 2,
                            "date": "2022-08-03",
                            "totalPayment": 10700.31,
                            "interestPayment": 1131.87,
                            "debtPayment": 9568.44,
                            "remainingDebt": 80981.25
                        },
                        {
                            "number": 3,
                            "date": "2022-09-03",
                            "totalPayment": 10700.31,
                            "interestPayment": 1012.27,
                            "debtPayment": 9688.04,
                            "remainingDebt": 71293.21
                        },
                        {
                            "number": 4,
                            "date": "2022-10-03",
                            "totalPayment": 10700.31,
                            "interestPayment": 891.17,
                            "debtPayment": 9809.14,
                            "remainingDebt": 61484.07
                        },
                        {
                            "number": 5,
                            "date": "2022-11-03",
                            "totalPayment": 10700.31,
                            "interestPayment": 768.55,
                            "debtPayment": 9931.76,
                            "remainingDebt": 51552.31
                        },
                        {
                            "number": 6,
                            "date": "2022-12-03",
                            "totalPayment": 10700.31,
                            "interestPayment": 644.40,
                            "debtPayment": 10055.91,
                            "remainingDebt": 41496.40
                        },
                        {
                            "number": 7,
                            "date": "2023-01-03",
                            "totalPayment": 10700.31,
                            "interestPayment": 518.71,
                            "debtPayment": 10181.60,
                            "remainingDebt": 31314.80
                        },
                        {
                            "number": 8,
                            "date": "2023-02-03",
                            "totalPayment": 10700.31,
                            "interestPayment": 391.44,
                            "debtPayment": 10308.87,
                            "remainingDebt": 21005.93
                        },
                        {
                            "number": 9,
                            "date": "2023-03-03",
                            "totalPayment": 10700.31,
                            "interestPayment": 262.57,
                            "debtPayment": 10437.74,
                            "remainingDebt": 10568.19
                        },
                        {
                            "number": 10,
                            "date": "2023-04-03",
                            "totalPayment": 10700.29,
                            "interestPayment": 132.10,
                            "debtPayment": 10568.19,
                            "remainingDebt": 0.00
                        }
                    ]
                }
            """.trimIndent()
        }

        fun scoringDataResponseObject(): CreditDTO {
            val mapper = jacksonObjectMapper()
            mapper.registerModule(JavaTimeModule())

            return mapper.readValue(scoringDataResponseJSON())
        }


        //+++++++++++++++++++++++++++++
        // CREDIT ENTITY
        //+++++++++++++++++++++++++++++

        private fun creditEntityExpectedToSaveInDBJSON(): String {

            return """
                {
                    "amount":    100000.00,
                    "term":  10,
                    "monthlyPayment":  10700.31,
                    "rate":  15.0,
                    "psk":   15.72,
                    "paymentSchedule": 
                        [
                            {"date": [2022, 7, 3], "number": 1, "debtPayment": 9450.31, "totalPayment": 10700.31, "remainingDebt": 90549.69, "interestPayment": 1250.00}, 
                            {"date": [2022, 8, 3], "number": 2, "debtPayment": 9568.44, "totalPayment": 10700.31, "remainingDebt": 80981.25, "interestPayment": 1131.87}, 
                            {"date": [2022, 9, 3], "number": 3, "debtPayment": 9688.04, "totalPayment": 10700.31, "remainingDebt": 71293.21, "interestPayment": 1012.27}, 
                            {"date": [2022, 10, 3], "number": 4, "debtPayment": 9809.14, "totalPayment": 10700.31, "remainingDebt": 61484.07, "interestPayment": 891.17}, 
                            {"date": [2022, 11, 3], "number": 5, "debtPayment": 9931.76, "totalPayment": 10700.31, "remainingDebt": 51552.31, "interestPayment": 768.55}, 
                            {"date": [2022, 12, 3], "number": 6, "debtPayment": 10055.91, "totalPayment": 10700.31, "remainingDebt": 41496.40, "interestPayment": 644.40}, 
                            {"date": [2023, 1, 3], "number": 7, "debtPayment": 10181.60, "totalPayment": 10700.31, "remainingDebt": 31314.80, "interestPayment": 518.71}, 
                            {"date": [2023, 2, 3], "number": 8, "debtPayment": 10308.87, "totalPayment": 10700.31, "remainingDebt": 21005.93, "interestPayment": 391.44}, 
                            {"date": [2023, 3, 3], "number": 9, "debtPayment": 10437.74, "totalPayment": 10700.31, "remainingDebt": 10568.19, "interestPayment": 262.57}, 
                            {"date": [2023, 4, 3], "number": 10, "debtPayment": 10568.19, "totalPayment": 10700.29, "remainingDebt": 0.00, "interestPayment": 132.10}
                        ],
                    "additionalServices":    
                        {
                            "isSalaryClient": true, 
                            "isInsuranceEnabled": true
                        },
                    "creditStatus": "CALCULATED"
                }
            """.trimIndent()
        }

        fun creditEntityExpectedToSaveInDBWithoutIdObject(): CreditEntity {
            val mapper = jacksonObjectMapper()
            mapper.registerModule(JavaTimeModule())

            return mapper.readValue(creditEntityExpectedToSaveInDBJSON())
        }

        fun creditEntityExpectedToSaveInDBWithIdObject(): CreditEntity {
            val creditEntity = creditEntityExpectedToSaveInDBWithoutIdObject()
            creditEntity.id = 16
            return creditEntity
        }

    }
}