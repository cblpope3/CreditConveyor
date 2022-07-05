package ru.leonov.deal.test_data

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.leonov.deal.dto.LoanOfferDTO

class LoanOfferTestData {
    companion object {
        fun fineRequestJSON(): String {
            return """
                    {
                        "applicationId": 29,
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

            return mapper.readValue(fineRequestJSON())
        }

        fun noApplicationIdRequestJSON(): String {
            return """
                    {
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

        fun noRAmountRequestJSON(): String {
            return """
                    {
                        "applicationId": 29,
                        "totalAmount": 669439.33,
                        "term": 7,
                        "monthlyPayment": 95634.19,
                        "rate": 12.0,
                        "isInsuranceEnabled": false,
                        "isSalaryClient": true
                    }
            """.trimIndent()
        }

        fun noTAmountRequestJSON(): String {
            return """
                    {
                        "applicationId": 29,
                        "requestedAmount": 643445.43,
                        "term": 7,
                        "monthlyPayment": 95634.19,
                        "rate": 12.0,
                        "isInsuranceEnabled": false,
                        "isSalaryClient": true
                    }
            """.trimIndent()
        }

        fun noTermRequestJSON(): String {
            return """
                    {
                        "applicationId": 29,
                        "requestedAmount": 643445.43,
                        "totalAmount": 669439.33,
                        "monthlyPayment": 95634.19,
                        "rate": 12.0,
                        "isInsuranceEnabled": false,
                        "isSalaryClient": true
                    }
            """.trimIndent()
        }

        fun noMPaymentRequestJSON(): String {
            return """
                    {
                        "applicationId": 29,
                        "requestedAmount": 643445.43,
                        "totalAmount": 669439.33,
                        "term": 7,
                        "rate": 12.0,
                        "isInsuranceEnabled": false,
                        "isSalaryClient": true
                    }
            """.trimIndent()
        }

        fun noRateRequestJSON(): String {
            return """
                    {
                        "applicationId": 29,
                        "requestedAmount": 643445.43,
                        "totalAmount": 669439.33,
                        "term": 7,
                        "monthlyPayment": 95634.19,
                        "isInsuranceEnabled": false,
                        "isSalaryClient": true
                    }
            """.trimIndent()
        }

        fun noInsuranceRequestJSON(): String {
            return """
                    {
                        "applicationId": 29,
                        "requestedAmount": 643445.43,
                        "totalAmount": 669439.33,
                        "term": 7,
                        "monthlyPayment": 95634.19,
                        "rate": 12.0,
                        "isSalaryClient": true
                    }
            """.trimIndent()
        }

        fun noSalaryRequestJSON(): String {
            return """
                    {
                        "applicationId": 29,
                        "requestedAmount": 643445.43,
                        "totalAmount": 669439.33,
                        "term": 7,
                        "monthlyPayment": 95634.19,
                        "rate": 12.0,
                        "isInsuranceEnabled": false
                    }
            """.trimIndent()
        }
    }
}