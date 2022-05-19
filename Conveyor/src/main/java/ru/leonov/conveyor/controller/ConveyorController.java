package ru.leonov.conveyor.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.leonov.conveyor.dto.CreditDTO;
import ru.leonov.conveyor.dto.LoanApplicationRequestDTO;
import ru.leonov.conveyor.dto.LoanOfferDTO;
import ru.leonov.conveyor.dto.ScoringDataDTO;
import ru.leonov.conveyor.exceptions.ScoringException;
import ru.leonov.conveyor.service.PreScoringService;
import ru.leonov.conveyor.service.ScoringService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    public ResponseEntity<CreditDTO> postConveyorCalculation(ScoringDataDTO scoringDataDTO) {

        if (log.isDebugEnabled()) log.debug("Got /conveyor/calculation request.");

        try {
            CreditDTO credit = scoringService.calculateCredit(scoringDataDTO);
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
    public ResponseEntity<List<LoanOfferDTO>> postConveyorOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        if (log.isDebugEnabled()) log.debug("got post conveyor offers request");

        List<LoanOfferDTO> possibleCreditOffers = preScoringService.getCreditOfferList(loanApplicationRequestDTO);
        if (log.isDebugEnabled()) log.debug("Returning response to request.");
        return new ResponseEntity<>(possibleCreditOffers, HttpStatus.OK);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<List<ErrorResponseContainer>> validationError(MethodArgumentNotValidException ex) {

        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        List<ErrorResponseContainer> errorResponseContainerList = fieldErrors.stream()
                .map(fieldError -> ErrorResponseContainer.builder()
                        .problemFieldName(fieldError.getField())
                        .rejectedValue(Objects.toString(fieldError.getRejectedValue(), "Not defined."))
                        .problemMessage(fieldError.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        log.debug("Got bad request: \n{}", errorResponseContainerList);

        return new ResponseEntity<>(errorResponseContainerList, HttpStatus.BAD_REQUEST);
    }
}

@Builder
@Getter
@ToString
class ErrorResponseContainer {

    private String problemFieldName;
    private String rejectedValue;
    private String problemMessage;
}
