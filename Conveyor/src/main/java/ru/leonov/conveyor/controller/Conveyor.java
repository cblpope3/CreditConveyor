package ru.leonov.conveyor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@SuppressWarnings("unused")
public class Conveyor implements DefaultApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PreScoringService preScoringService;
    private final ScoringService scoringService;

    @Autowired
    public Conveyor(PreScoringService preScoringService, ScoringService scoringService) {
        this.preScoringService = preScoringService;
        this.scoringService = scoringService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ModelsCreditDTO> postConveyorCalculation(ModelsScoringDataDTO modelsScoringDataDTO) {

        if (logger.isTraceEnabled()) logger.trace("Got /conveyor/calculation request.");

        try {
            ModelsCreditDTO credit = scoringService.calculateCredit(modelsScoringDataDTO);
            if (logger.isDebugEnabled()) logger.debug("Credit calculated, returning response.");
            return new ResponseEntity<>(credit, HttpStatus.OK);
        } catch (ScoringException e) {
            if (logger.isDebugEnabled()) logger.debug("Credit denied. Reason: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<ModelsLoanOfferDTO>> postConveyorOffers(ModelsLoanApplicationRequestDTO modelsLoanApplicationRequestDTO) {
        if (logger.isTraceEnabled()) logger.trace("got post conveyor offers request");

        try {
            List<ModelsLoanOfferDTO> possibleCreditOffers = preScoringService.getCreditOfferList(modelsLoanApplicationRequestDTO);
            if (logger.isDebugEnabled()) logger.debug("Returning response to request.");
            return new ResponseEntity<>(possibleCreditOffers, HttpStatus.OK);
        } catch (LoanRequestException e) {
            if (logger.isWarnEnabled()) logger.warn("Request not valid: {}", e.getMessage());
            HttpHeaders headers = new HttpHeaders();
            headers.add("cause", e.getMessage());
            return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
        }
    }
}
