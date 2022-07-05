package ru.leonov.deal.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import ru.leonov.deal.dto.CreditDTO
import ru.leonov.deal.dto.LoanApplicationRequestDTO
import ru.leonov.deal.dto.LoanOfferDTO
import ru.leonov.deal.dto.ScoringDataDTO

@FeignClient(name = "conveyor-app-client", url = "\${conveyor-app.hostname}")
interface ConveyorAppClient {

    @PostMapping(value = ["/conveyor/offers"])
    fun requestLoanOffer(requestDTO: LoanApplicationRequestDTO): MutableList<LoanOfferDTO>?

    @PostMapping(value = ["/conveyor/calculation"])
    fun requestCreditCalculation(scoringDataDTO: ScoringDataDTO): CreditDTO?
}
