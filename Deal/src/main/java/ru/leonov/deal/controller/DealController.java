package ru.leonov.deal.controller;

import kotlin.Unit;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.leonov.deal.dto.LoanApplicationRequestDTO;
import ru.leonov.deal.dto.LoanOfferDTO;
import ru.leonov.deal.dto.ScoringDataDTO;
import ru.leonov.deal.exceptions.ApplyOfferException;
import ru.leonov.deal.service.ApplyOfferService;
import ru.leonov.deal.service.GetOffersService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controller that handle '/deal' rest requests.
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RestController
public class DealController implements DealApi {

    private final GetOffersService getOffersService;
    private final ApplyOfferService applyOfferService;

    /**
     * Request of loan offer list.
     *
     * @param loanApplicationRequestDTO pre-scoring data of request.
     * @return {@link List} of generated loan offers. Sorted from worst to best.
     */
    @NotNull
    @Override
    public ResponseEntity<List<LoanOfferDTO>> postDealApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        log.debug("Got loan offers request: {}", loanApplicationRequestDTO.toString());

        List<LoanOfferDTO> responseOfferList = getOffersService.getOffers(loanApplicationRequestDTO);

        log.debug("Offers list created. Returning response.");
        log.trace("Created offers list: {}", responseOfferList.toString());

        return new ResponseEntity<>(responseOfferList, HttpStatus.OK);
    }

    /**
     * Apply one of previously received loan offers.
     *
     * @param loanOfferDTO loan offer that is have to be applied.
     * @return Code 200 if loan offer applied successfully.
     */
    @NotNull
    @Override
    public ResponseEntity<Unit> putDealOffer(LoanOfferDTO loanOfferDTO) {

        log.debug("Got apply offer request: {}", loanOfferDTO.toString());

        applyOfferService.applyOffer(loanOfferDTO);

        log.debug("Requested offer request processed successfully.");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Method handle issues that happened during loan offer application.
     *
     * @param e exception that happened during loan offer application process.
     * @return {@link String} with problem explanation.
     */
    @ExceptionHandler(ApplyOfferException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> putDealOfferBadRequest(ApplyOfferException e) {
        String responseMessage = e.getMessage();
        if (e.getExceptionCause() == ApplyOfferException.ExceptionCause.APPLICATION_NOT_FOUND) {
            log.debug("Not found application with requested id.");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        } else {
            log.debug("Got unexpected exception: {}.", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @NotNull
    @Override
    public ResponseEntity<Unit> putDealCalculate(int applicationId, @Nullable ScoringDataDTO scoringDataDTO) {
        //todo implement this
        return null;
    }

    /**
     * Method that performs request parameter validation.
     *
     * @param ex {@link MethodArgumentNotValidException} that was thrown during validation.
     * @return {@link List} of {@link ErrorResponseContainer} with happened issues explanation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
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

/**
 * Container that used to create error explanation json.
 */
@Builder
@Getter
@ToString
class ErrorResponseContainer {

    private String problemFieldName;
    private String rejectedValue;
    private String problemMessage;
}
