package ru.leonov.conveyor.test_data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import ru.leonov.conveyor.dto.LoanApplicationRequestDTO;
import ru.leonov.conveyor.dto.LoanOfferDTO;

import java.util.List;

public class LoanOfferTestData {

    //this json is copied from openapi spec example.
    @Getter
    private static final String fineLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String noAmountLoanOfferRequestJSON = """
            {
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String wrongAmountLoanOfferRequestJSON = """
            {
              "amount": 1000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String noTermLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String wrongTermLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 3,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String noFirstNameLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String wrongFirstNameLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy23",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String noLastNameLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String wrongLastNameLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin33",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String noMiddleNameLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String wrongMiddleNameLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich55",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String noEmailLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String wrongEmailLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "azaza",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String noBirthdateLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String wrongBirthdateLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "12-05-2004",
              "passportSeries": "5337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String noPassportSeriesLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String wrongPassportSeriesLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "53337",
              "passportNumber": "345345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String noPassportNumberLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String wrongPassportNumberLoanOfferRequestJSON = """
            {
              "amount": 30000.0,
              "term": 6,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "email": "uluk_poop@vasya.ru",
              "birthdate": "2004-05-12",
              "passportSeries": "5337",
              "passportNumber": "35345"
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String fineLoanOfferResponseJSON = """
            [
                {
                    "applicationId": 590311,
                    "requestedAmount": 30000.0,
                    "totalAmount": 31326.06,
                    "term": 6,
                    "monthlyPayment": 5221.01,
                    "rate": 15.0,
                    "isInsuranceEnabled": false,
                    "isSalaryClient": false
                },
                {
                    "applicationId": 573802,
                    "requestedAmount": 30000.0,
                    "totalAmount": 131236.84,
                    "term": 6,
                    "monthlyPayment": 5206.14,
                    "rate": 14.0,
                    "isInsuranceEnabled": true,
                    "isSalaryClient": false
                },
                {
                    "applicationId": 921379,
                    "requestedAmount": 30000.0,
                    "totalAmount": 31058.70,
                    "term": 6,
                    "monthlyPayment": 5176.45,
                    "rate": 12.0,
                    "isInsuranceEnabled": false,
                    "isSalaryClient": true
                },
                {
                    "applicationId": 12600,
                    "requestedAmount": 30000.0,
                    "totalAmount": 130969.84,
                    "term": 6,
                    "monthlyPayment": 5161.64,
                    "rate": 11.0,
                    "isInsuranceEnabled": true,
                    "isSalaryClient": true
                }
            ]""";

    public static LoanApplicationRequestDTO getFineLoanOfferRequest() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            return mapper.readValue(fineLoanOfferRequestJSON, LoanApplicationRequestDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cant parse json");
        }
    }

    public static LoanApplicationRequestDTO getNoMiddleNameLoanOfferRequest() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            return mapper.readValue(noMiddleNameLoanOfferRequestJSON, LoanApplicationRequestDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cant parse json");
        }
    }

    public static List<LoanOfferDTO> getFineLoanOfferResponse() {

        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(fineLoanOfferResponseJSON, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cant parse json");
        }
    }
}
