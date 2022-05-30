package ru.leonov.deal.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.leonov.deal.dto.LoanApplicationRequestDTO;
import ru.leonov.deal.dto.LoanOfferDTO;

import java.util.List;

@FeignClient(name = "pre-scoring-client", url = "${conveyor-app.hostname}")
public interface PreScoringClient {

    @PostMapping(value = "/conveyor/offers")
    List<LoanOfferDTO> requestLoanOffer(LoanApplicationRequestDTO requestDTO);

}
