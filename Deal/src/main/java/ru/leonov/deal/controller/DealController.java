package ru.leonov.deal.controller;

import kotlin.Unit;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.leonov.deal.dto.LoanApplicationRequestDTO;
import ru.leonov.deal.dto.LoanOfferDTO;
import ru.leonov.deal.dto.ScoringDataDTO;
import ru.leonov.deal.service.GetOffersService;

import java.util.List;

//todo write javadoc
@Slf4j
@SuppressWarnings("unused")
@RestController
public class DealController implements DealApi {

    @Autowired
    GetOffersService getOffersService;

    @NotNull
    @Override
    public ResponseEntity<List<LoanOfferDTO>> postDealApplication(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        log.debug("Got deal application request: {}", loanApplicationRequestDTO.toString());

        List<LoanOfferDTO> responseOfferList = getOffersService.getOffers(loanApplicationRequestDTO);

        log.debug("Returning offers list: {}", responseOfferList.toString());

        //todo handle error status codes
        return new ResponseEntity<>(responseOfferList, HttpStatus.OK);

    }

    @NotNull
    @Override
    public ResponseEntity<Unit> putDealOffer(@Nullable LoanOfferDTO loanOfferDTO) {
        //todo implement this
        return null;
    }

    @NotNull
    @Override
    public ResponseEntity<Unit> putDealCalculate(int applicationId, @Nullable ScoringDataDTO scoringDataDTO) {
        //todo implement this
        return null;
    }
}
