package ru.leonov.conveyor.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.leonov.conveyor.exceptions.LoanRequestException;
import ru.leonov.conveyor.service.PreScoringService;
import ru.leonov.conveyor.service.ScoringService;
import ru.leonov.conveyor.test_data.LoanOfferTestData;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ru.leonov.conveyor.controller.ConveyorController.class)
class ConveyorOffersControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PreScoringService preScoringService;

    @SuppressWarnings("unused")
    @MockBean
    ScoringService scoringService;

    @Test
    void postConveyorOffersFine() throws Exception {

        when(preScoringService.getCreditOfferList(LoanOfferTestData.getFineLoanOfferRequest()))
                .thenReturn(LoanOfferTestData.getFineLoanOfferResponse());

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getFineLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(LoanOfferTestData.getFineLoanOfferResponseJSON()));

        verify(preScoringService, times(1)).getCreditOfferList(LoanOfferTestData.getFineLoanOfferRequest());
    }

    //testing bad request response
    @Test
    void postConveyorOffersException() throws Exception {

        when(preScoringService.getCreditOfferList(LoanOfferTestData.getFineLoanOfferRequest()))
                .thenThrow(new LoanRequestException(LoanRequestException.ExceptionCause.PERSON_TOO_YOUNG, "2"));

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getFineLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("cause", containsString("Person's age is less than 18 years.")));

        verify(preScoringService, times(1)).getCreditOfferList(LoanOfferTestData.getFineLoanOfferRequest());
    }
}