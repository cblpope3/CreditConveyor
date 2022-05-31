package ru.leonov.deal.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.leonov.deal.dto.CreditDTO;
import ru.leonov.deal.dto.LoanApplicationRequestDTO;
import ru.leonov.deal.dto.LoanOfferDTO;
import ru.leonov.deal.dto.ScoringDataDTO;

import java.util.List;

@FeignClient(name = "conveyor-app-client", url = "${conveyor-app.hostname}")
public interface ConveyorAppClient {

    @PostMapping(value = "/conveyor/offers")
    List<LoanOfferDTO> requestLoanOffer(LoanApplicationRequestDTO requestDTO);

    @PostMapping(value = "/conveyor/calculation")
    CreditDTO requestCreditCalculation(ScoringDataDTO scoringDataDTO);

}
