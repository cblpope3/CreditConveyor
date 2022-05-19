package ru.leonov.conveyor.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.leonov.conveyor.service.PreScoringService;
import ru.leonov.conveyor.service.ScoringService;
import ru.leonov.conveyor.test_data.LoanOfferTestData;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void postConveyorOffersNoMiddleName() throws Exception {

        when(preScoringService.getCreditOfferList(LoanOfferTestData.getNoMiddleNameLoanOfferRequest()))
                .thenReturn(LoanOfferTestData.getFineLoanOfferResponse());

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getNoMiddleNameLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(LoanOfferTestData.getFineLoanOfferResponseJSON()));

        verify(preScoringService, times(1)).getCreditOfferList(LoanOfferTestData.getNoMiddleNameLoanOfferRequest());
    }

    //testing bad request responses

    @Test
    void postConveyorOffersNoAmount() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getNoAmountLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersWrongAmount() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getWrongAmountLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersNoTerm() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getNoTermLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersWrongTerm() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getWrongTermLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersNoFirstName() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getNoFirstNameLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersWrongFirstName() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getWrongFirstNameLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersNoLastName() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getNoLastNameLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersWrongLastName() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getWrongLastNameLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersWrongMiddleName() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getWrongMiddleNameLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersNoEmail() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getNoEmailLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersWrongEmail() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getWrongEmailLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersNoBirthdate() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getNoBirthdateLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersWrongBirthdate() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getWrongBirthdateLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersNoPassportSeries() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getNoPassportSeriesLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersWrongPassportSeries() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getWrongPassportSeriesLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersNoPassportNumber() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getNoPassportNumberLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }

    @Test
    void postConveyorOffersWrongPassportNumber() throws Exception {

        this.mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LoanOfferTestData.getWrongPassportNumberLoanOfferRequestJSON()))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(preScoringService, times(0)).getCreditOfferList(any());
    }
}