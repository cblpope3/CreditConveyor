package ru.leonov.conveyor.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.leonov.conveyor.dto.CreditDTO;
import ru.leonov.conveyor.dto.ScoringDataDTO;
import ru.leonov.conveyor.exceptions.ScoringException;
import ru.leonov.conveyor.service.CreditCalculationService;
import ru.leonov.conveyor.service.ScoringService;

import java.math.BigDecimal;

/**
 * Class that combines scoring service and credit calculation service action together.
 */
@Component
@RequiredArgsConstructor
public class CreditCalculationFacade {

    private final ScoringService scoringService;
    private final CreditCalculationService creditCalculationService;

    /**
     * Perform credit calculation based on scoring data.
     *
     * @param scoringDataDTO credit calculation request data.
     * @return calculated credit.
     * @throws ScoringException if credit hadn't been approved.
     */
    public CreditDTO calculateCredit(ScoringDataDTO scoringDataDTO) throws ScoringException {
        BigDecimal creditRate = scoringService.calculateRate(scoringDataDTO);
        return creditCalculationService.calculateCredit(scoringDataDTO.getAmount(), creditRate, scoringDataDTO.getTerm(),
                scoringDataDTO.getIsInsuranceEnabled(), scoringDataDTO.getIsSalaryClient());
    }

}
