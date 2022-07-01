package ru.leonov.deal.test_data

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.leonov.deal.dto.LoanApplicationRequestDTO
import ru.leonov.deal.dto.LoanOfferDTO

class LoanRequestTestData {

    companion object {
        fun fineRequestJSON(): String {
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

        fun noAmountRequestJSON(): String {
            return """{
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

        fun wrongAmountRequestJSON(): String {
            return """{
                      "amount": 10.00,
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

        fun noTermRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun wrongTermRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 1,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun noFNameRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun wrongFNameRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "!Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun noLNameRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun wrongLNameRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "lastName": "!Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun noMNameRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun wrongMNameRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "!Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun noEmailRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun wrongEmailRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoopulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun noBirthDateRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun wrongBirthDateRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-28-28",
                      "passportSeries": "2467",
                      "passportNumber": "565732"
                    }"""
        }

        fun noPSeriesRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportNumber": "565732"
                    }"""
        }

        fun wrongPSeriesRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "s67",
                      "passportNumber": "565732"
                    }"""
        }

        fun noPNumberRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467"
                    }"""
        }

        fun wrongPNumberRequestJSON(): String {
            return """{
                      "amount": 100000.00,
                      "term": 10,
                      "firstName": "Vasiliy",
                      "lastName": "Pupkin",
                      "middleName": "Ulukbekovich",
                      "email": "vasyapoop@ulukbek.ru",
                      "birthdate": "1998-04-28",
                      "passportSeries": "2467",
                      "passportNumber": "f5732"
                    }"""
        }

        fun fineResponseJSON(): String {
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

        fun fineRequestObject(): LoanApplicationRequestDTO {
            val mapper = jacksonObjectMapper()
            mapper.registerModule(JavaTimeModule())

            return mapper.readValue(fineRequestJSON())
        }

        fun fineResponseObject(): List<LoanOfferDTO> {
            val mapper = jacksonObjectMapper()
            return mapper.readValue(fineResponseJSON())
        }

        fun noMNameRequestObject(): LoanApplicationRequestDTO {
            val mapper = jacksonObjectMapper()
            mapper.registerModule(JavaTimeModule())

            return mapper.readValue(noMNameRequestJSON())
        }

    }
}
