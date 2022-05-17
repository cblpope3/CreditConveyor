package ru.leonov.conveyor.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.leonov.conveyor.dto.ModelsCreditDTO;
import ru.leonov.conveyor.dto.ModelsLoanApplicationRequestDTO;
import ru.leonov.conveyor.dto.ModelsLoanOfferDTO;
import ru.leonov.conveyor.dto.ModelsScoringDataDTO;
import ru.leonov.conveyor.exceptions.LoanRequestException;
import ru.leonov.conveyor.exceptions.ScoringException;
import ru.leonov.conveyor.service.PreScoringService;
import ru.leonov.conveyor.service.ScoringService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@SuppressWarnings("unused")
public class ConveyorController implements ConveyorApi {

    private final PreScoringService preScoringService;
    private final ScoringService scoringService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ModelsCreditDTO> postConveyorCalculation(ModelsScoringDataDTO modelsScoringDataDTO) {

        if (log.isTraceEnabled()) log.trace("Got /conveyor/calculation request.");

        try {
            ModelsCreditDTO credit = scoringService.calculateCredit(modelsScoringDataDTO);
            if (log.isDebugEnabled()) log.debug("Credit calculated, returning response.");
            return new ResponseEntity<>(credit, HttpStatus.OK);
        } catch (ScoringException e) {
            if (log.isDebugEnabled()) log.debug("Credit denied. Reason: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<ModelsLoanOfferDTO>> postConveyorOffers(ModelsLoanApplicationRequestDTO modelsLoanApplicationRequestDTO) {
        if (log.isTraceEnabled()) log.trace("got post conveyor offers request");

        try {
            List<ModelsLoanOfferDTO> possibleCreditOffers = preScoringService.getCreditOfferList(modelsLoanApplicationRequestDTO);
            if (log.isDebugEnabled()) log.debug("Returning response to request.");
            return new ResponseEntity<>(possibleCreditOffers, HttpStatus.OK);
        } catch (LoanRequestException e) {
            if (log.isWarnEnabled()) log.warn("Request not valid: {}", e.getMessage());
            HttpHeaders headers = new HttpHeaders();
            headers.add("cause", e.getMessage());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
    }
}
