package ru.leonov.conveyor.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.leonov.conveyor.exceptions.ScoringException;
import ru.leonov.conveyor.service.PreScoringService;
import ru.leonov.conveyor.service.ScoringService;
import ru.leonov.conveyor.test_data.LoanCalculationTestData;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ru.leonov.conveyor.controller.ConveyorController.class)
class ConveyorCalculationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @SuppressWarnings("unused")
    @MockBean
    PreScoringService preScoringService;

    @MockBean
    ScoringService scoringService;

    //testing fine request
    @Test
    void postConveyorCalculation() throws Exception {

        when(scoringService.calculateCredit(LoanCalculationTestData.getFineLoanCalculationRequestObject()))
                .thenReturn(LoanCalculationTestData.getFineLoanCalculationResponseObject());

        String expectedResponse = LoanCalculationTestData.getFineLoanCalculationResponseJSON();

        mockMvc.perform(post("/conveyor/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanCalculationTestData.getExampleLoanCalculationRequestJSON()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));

        verify(scoringService, times(1))
                .calculateCredit(LoanCalculationTestData.getFineLoanCalculationRequestObject());
    }

    //testing bad request response
    @Test
    void postConveyorCalculationException() throws Exception {

        when(scoringService.calculateCredit(LoanCalculationTestData.getFineLoanCalculationRequestObject()))
                .thenThrow(new ScoringException(ScoringException.ExceptionCause.UNACCEPTABLE_EMPLOYER_STATUS));

        mockMvc.perform(post("/conveyor/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanCalculationTestData.getExampleLoanCalculationRequestJSON()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(scoringService, times(1))
                .calculateCredit(LoanCalculationTestData.getFineLoanCalculationRequestObject());
    }
}