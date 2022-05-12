package ru.leonov.conveyor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.leonov.conveyor.dto.ModelsCreditDTO;
import ru.leonov.conveyor.dto.ModelsLoanApplicationRequestDTO;
import ru.leonov.conveyor.dto.ModelsLoanOfferDTO;
import ru.leonov.conveyor.dto.ModelsScoringDataDTO;

import java.util.List;

@RestController
@SuppressWarnings("unused")
public class Conveyor implements DefaultApi {

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<ModelsCreditDTO> postConveyorCalculation(ModelsScoringDataDTO modelsScoringDataDTO) {
        //todo implement this
        System.out.println("got post conveyor calculation request");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<List<ModelsLoanOfferDTO>> postConveyorOffers(ModelsLoanApplicationRequestDTO modelsLoanApplicationRequestDTO) {
        //todo implement this
        System.out.println("got post conveyor offers request");
        return null;
    }
}
