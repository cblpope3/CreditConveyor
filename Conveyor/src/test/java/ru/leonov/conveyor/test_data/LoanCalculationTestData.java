package ru.leonov.conveyor.test_data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import ru.leonov.conveyor.dto.CreditDTO;
import ru.leonov.conveyor.dto.PaymentScheduleElementDTO;
import ru.leonov.conveyor.dto.ScoringDataDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanCalculationTestData {

    //this json is copied from openapi spec example.
    @Getter
    private static final String exampleLoanCalculationRequestJSON = """
            {
              "amount": 30000,
              "term": 10,
              "firstName": "Vasiliy",
              "lastName": "Pupkin",
              "middleName": "Ulukbekovich",
              "gender": "MALE",
              "birthdate": "1965-08-24",
              "passportSeries": "2356",
              "passportNumber": "234266",
              "passportIssueDate": "2019-08-24",
              "passportIssueBranch": "Бобруйский УВД №66",
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
              "account": "23424634665586",
              "isInsuranceEnabled": false,
              "isSalaryClient": true
            }
            """;

    //this json is copied from openapi spec example.
    @Getter
    private static final String exampleLoanCalculationResponseJSON = """
            {
                "amount": 30000,
                "term": 10,
                "monthlyPayment": 3210.09,
                "rate": 15.0,
                "psk": 15.72,
                "isInsuranceEnabled": false,
                "isSalaryClient": true,
                "paymentSchedule": [
                    {
                        "number": 1,
                        "date": "2022-06-20",
                        "totalPayment": 3210.09,
                        "interestPayment": 375.00,
                        "debtPayment": 2835.09,
                        "remainingDebt": 27164.91
                    },
                    {
                        "number": 2,
                        "date": "2022-07-20",
                        "totalPayment": 3210.09,
                        "interestPayment": 339.56,
                        "debtPayment": 2870.53,
                        "remainingDebt": 24294.38
                    },
                    {
                        "number": 3,
                        "date": "2022-08-20",
                        "totalPayment": 3210.09,
                        "interestPayment": 303.68,
                        "debtPayment": 2906.41,
                        "remainingDebt": 21387.97
                    },
                    {
                        "number": 4,
                        "date": "2022-09-20",
                        "totalPayment": 3210.09,
                        "interestPayment": 267.35,
                        "debtPayment": 2942.74,
                        "remainingDebt": 18445.23
                    },
                    {
                        "number": 5,
                        "date": "2022-10-20",
                        "totalPayment": 3210.09,
                        "interestPayment": 230.57,
                        "debtPayment": 2979.52,
                        "remainingDebt": 15465.71
                    },
                    {
                        "number": 6,
                        "date": "2022-11-20",
                        "totalPayment": 3210.09,
                        "interestPayment": 193.32,
                        "debtPayment": 3016.77,
                        "remainingDebt": 12448.94
                    },
                    {
                        "number": 7,
                        "date": "2022-12-20",
                        "totalPayment": 3210.09,
                        "interestPayment": 155.61,
                        "debtPayment": 3054.48,
                        "remainingDebt": 9394.46
                    },
                    {
                        "number": 8,
                        "date": "2023-01-20",
                        "totalPayment": 3210.09,
                        "interestPayment": 117.43,
                        "debtPayment": 3092.66,
                        "remainingDebt": 6301.80
                    },
                    {
                        "number": 9,
                        "date": "2023-02-20",
                        "totalPayment": 3210.09,
                        "interestPayment": 78.77,
                        "debtPayment": 3131.32,
                        "remainingDebt": 3170.48
                    },
                    {
                        "number": 10,
                        "date": "2023-03-20",
                        "totalPayment": 3210.11,
                        "interestPayment": 39.63,
                        "debtPayment": 3170.48,
                        "remainingDebt": 0.00
                    }
                ]
            }
             """;

    /**
     * Get filled with correct data {@link ScoringDataDTO} object. Can be used as test request for
     * credit calculation service. Correct response must be {@link #getFineLoanCalculationResponseObject()}.
     *
     * @return data for scoring that is generated from example json.
     * @see #getFineLoanCalculationResponseObject()
     */
    public static ScoringDataDTO getFineLoanCalculationRequestObject() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            return mapper.readValue(exampleLoanCalculationRequestJSON, ScoringDataDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cant parse json");
        }
    }

    /**
     * Get filled with correct data {@link CreditDTO} object. Can be used as test request for
     * credit calculation service. Correct request to get this response is {@link #getFineLoanCalculationRequestObject()}.
     * <b>Dates in payments schedule will be as if loan had been retrieved today.</b>
     *
     * @return calculated credit data that is generated from example json <b>from current date</b>.
     * @see #getFineLoanCalculationRequestObject() ()
     */
    public static CreditDTO getFineLoanCalculationResponseObject() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            CreditDTO calculationResponse = mapper.readValue(exampleLoanCalculationResponseJSON, CreditDTO.class);

            //replacing dates in payment schedule with today dates.
            calculationResponse.setPaymentSchedule(
                    replacePaymentDatesFromToday(calculationResponse.getPaymentSchedule())
            );

            return calculationResponse;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cant parse json");
        }
    }

    /**
     * Method returns {@link #getFineLoanCalculationResponseObject()} as JSON.
     *
     * @return json object for testing purposes.
     * @see #getFineLoanCalculationResponseObject()
     */
    public static String getFineLoanCalculationResponseJSON() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        try {
            return mapper.writeValueAsString(getFineLoanCalculationResponseObject());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cant parse json");
        }
    }

    /**
     * Method take {@link List} of {@link PaymentScheduleElementDTO} and replaces dates of payments as if loan had
     * been started today.
     *
     * @param sourceSchedule payment schedule with incorrect dates.
     * @return payment schedule that starting from today.
     */
    private static List<PaymentScheduleElementDTO> replacePaymentDatesFromToday(List<PaymentScheduleElementDTO> sourceSchedule) {
        List<PaymentScheduleElementDTO> scheduleFromToday = new ArrayList<>();
        for (int i = 0; i < sourceSchedule.size(); i++) {
            PaymentScheduleElementDTO payment = sourceSchedule.get(i);
            payment.setDate(LocalDate.now().plusMonths(i + 1));
            scheduleFromToday.add(payment);
        }
        return scheduleFromToday;
    }
}
