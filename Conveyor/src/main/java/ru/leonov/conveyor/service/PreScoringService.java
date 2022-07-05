package ru.leonov.conveyor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.leonov.conveyor.dto.LoanApplicationRequestDTO;
import ru.leonov.conveyor.dto.LoanOfferDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service that perform pre-scoring job.
 */
@Slf4j
@Service
public class PreScoringService {

    private final CreditCalculationService creditCalculationService;
    private final BigDecimal baseRate;

    @Autowired
    public PreScoringService(@Value("${app-params.baseRate}") double baseRate,
                             CreditCalculationService creditCalculationService) {
        this.baseRate = BigDecimal.valueOf(baseRate);
        this.creditCalculationService = creditCalculationService;
    }

    /**
     * This method calculating four credit offers to customer.
     *
     * @param loanRequest requested credit.
     * @return {@link List} of four credit offers.
     */
    public List<LoanOfferDTO> getCreditOfferList(LoanApplicationRequestDTO loanRequest) {

        return creditCalculationService.generateCreditOffers(loanRequest.getAmount(), loanRequest.getTerm(),
                baseRate);

    }

}
