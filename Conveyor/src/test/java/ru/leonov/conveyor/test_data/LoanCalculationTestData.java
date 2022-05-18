package ru.leonov.conveyor.test_data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import ru.leonov.conveyor.dto.CreditDTO;
import ru.leonov.conveyor.dto.ScoringDataDTO;

public class LoanCalculationTestData {

    //this json is copied from openapi spec example.
    @Getter
    private static final String fineLoanCalculationRequestJSON = """
            {
              "amount": 30000,
              "term": 12,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "gender": "NON_BINARY",
              "birthdate": "1980-08-24",
              "passportSeries": "2356",
              "passportNumber": "234266",
              "passportIssueDate": "2019-08-24",
              "passportIssueBranch": "Бобруйский УВД №66",
              "maritalStatus": "DIVORCED",
              "dependentAmount": 2,
              "employment": {
                "employmentStatus": "EMPLOYED",
                "employerINN": "43646375637",
                "salary": 5501,
                "position": "TOP_MANAGER",
                "workExperienceTotal": 21,
                "workExperienceCurrent": 21
              },
              "account": "23424634665586",
              "isInsuranceEnabled": false,
              "isSalaryClient": true
            }""";

    //this json is copied from openapi spec example.
    @Getter
    private static final String fineLoanCalculationResponseJSON = """
            {
                 "amount": 30000,
                 "term": 12,
                 "monthlyPayment": 2721.93,
                 "rate": 16.0,
                 "psk": 16.92,
                 "isInsuranceEnabled": false,
                 "isSalaryClient": true,
                 "paymentSchedule": [
                     {
                         "number": 1,
                         "date": "2022-06-18",
                         "totalPayment": 2721.93,
                         "interestPayment": 400.00,
                         "debtPayment": 2321.93,
                         "remainingDebt": 27678.07
                     },
                     {
                         "number": 2,
                         "date": "2022-07-18",
                         "totalPayment": 2721.93,
                         "interestPayment": 369.04,
                         "debtPayment": 2352.89,
                         "remainingDebt": 25325.18
                     },
                     {
                         "number": 3,
                         "date": "2022-08-18",
                         "totalPayment": 2721.93,
                         "interestPayment": 337.67,
                         "debtPayment": 2384.26,
                         "remainingDebt": 22940.92
                     },
                     {
                         "number": 4,
                         "date": "2022-09-18",
                         "totalPayment": 2721.93,
                         "interestPayment": 305.88,
                         "debtPayment": 2416.05,
                         "remainingDebt": 20524.87
                     },
                     {
                         "number": 5,
                         "date": "2022-10-18",
                         "totalPayment": 2721.93,
                         "interestPayment": 273.66,
                         "debtPayment": 2448.27,
                         "remainingDebt": 18076.60
                     },
                     {
                         "number": 6,
                         "date": "2022-11-18",
                         "totalPayment": 2721.93,
                         "interestPayment": 241.02,
                         "debtPayment": 2480.91,
                         "remainingDebt": 15595.69
                     },
                     {
                         "number": 7,
                         "date": "2022-12-18",
                         "totalPayment": 2721.93,
                         "interestPayment": 207.94,
                         "debtPayment": 2513.99,
                         "remainingDebt": 13081.70
                     },
                     {
                         "number": 8,
                         "date": "2023-01-18",
                         "totalPayment": 2721.93,
                         "interestPayment": 174.42,
                         "debtPayment": 2547.51,
                         "remainingDebt": 10534.19
                     },
                     {
                         "number": 9,
                         "date": "2023-02-18",
                         "totalPayment": 2721.93,
                         "interestPayment": 140.46,
                         "debtPayment": 2581.47,
                         "remainingDebt": 7952.72
                     },
                     {
                         "number": 10,
                         "date": "2023-03-18",
                         "totalPayment": 2721.93,
                         "interestPayment": 106.04,
                         "debtPayment": 2615.89,
                         "remainingDebt": 5336.83
                     },
                     {
                         "number": 11,
                         "date": "2023-04-18",
                         "totalPayment": 2721.93,
                         "interestPayment": 71.16,
                         "debtPayment": 2650.77,
                         "remainingDebt": 2686.06
                     },
                     {
                         "number": 12,
                         "date": "2023-05-18",
                         "totalPayment": 2721.87,
                         "interestPayment": 35.81,
                         "debtPayment": 2686.06,
                         "remainingDebt": 0.00
                     }
                 ]
             }""";

    public static ScoringDataDTO getFineLoanCalculationRequest() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            return mapper.readValue(fineLoanCalculationRequestJSON, ScoringDataDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cant parse json");
        }
    }

    public static CreditDTO getFineLoanCalculationResponse() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            return mapper.readValue(fineLoanCalculationResponseJSON, CreditDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cant parse json");
        }
    }
}
