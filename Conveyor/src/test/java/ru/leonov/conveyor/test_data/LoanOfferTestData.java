package ru.leonov.conveyor.test_data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import ru.leonov.conveyor.dto.ModelsLoanApplicationRequestDTO;
import ru.leonov.conveyor.dto.ModelsLoanOfferDTO;

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

    public static ModelsLoanApplicationRequestDTO getFineLoanOfferRequest() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            return mapper.readValue(fineLoanOfferRequestJSON, ModelsLoanApplicationRequestDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cant parse json");
        }
    }

    public static List<ModelsLoanOfferDTO> getFineLoanOfferResponse() {

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
