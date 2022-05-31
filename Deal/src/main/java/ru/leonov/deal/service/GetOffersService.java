package ru.leonov.deal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.leonov.deal.client.PreScoringClient;
import ru.leonov.deal.dto.LoanApplicationRequestDTO;
import ru.leonov.deal.dto.LoanOfferDTO;
import ru.leonov.deal.mappers.LoanRequestMapper;
import ru.leonov.deal.model.entity.ApplicationEntity;
import ru.leonov.deal.model.entity.ClientEntity;
import ru.leonov.deal.repository.ApplicationRepository;
import ru.leonov.deal.repository.ClientRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import static ru.leonov.deal.utility.ApplicationUtility.createNewApplication;

/**
 * Service that handle loan application requests.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GetOffersService {

    private final PreScoringClient preScoringClient;
    private final ClientRepository clientRepository;
    private final ApplicationRepository applicationRepository;

    /**
     * Make credit offers list from loan application request.
     *
     * @param loanApplicationRequestDTO user's request.
     * @return credit offers generated by Conveyor-App.
     */
    public List<LoanOfferDTO> getOffers(LoanApplicationRequestDTO loanApplicationRequestDTO) {

        // 1.	По API приходит LoanApplicationRequestDTO
        // 2.	На основе LoanApplicationRequestDTO создаётся сущность Client и сохраняется в БД.
        ClientEntity newClient = saveNewClientToDatabase(loanApplicationRequestDTO);
        log.trace("New client #{} saved to database.", newClient.getId());

        // 3.	Создаётся Application со связью на только что созданный Client и сохраняется в БД.
        Long newApplicationId = saveNewApplicationToDatabase(newClient);
        log.trace("Application #{} saved.", newApplicationId);

        // 4.	Отправляется POST запрос на /conveyor/offers МС conveyor через FeignClient
        // (здесь и далее вместо FeignClient можно использовать RestTemplate).
        // Каждому элементу из списка List<LoanOfferDTO> присваивается id созданной заявки (Application)
        List<LoanOfferDTO> responseList = preScoringClient.requestLoanOffer(loanApplicationRequestDTO);
        log.trace("Got loan offer list from Conveyor app: {}", responseList);

        responseList.forEach(a -> a.setApplicationId(newApplicationId));

        // 5.	Ответ на API - список из 4-х LoanOfferDTO от "худшего" к "лучшему".
        Comparator<LoanOfferDTO> loanComparator = Comparator.comparing(
                LoanOfferDTO::getRate,
                BigDecimal::compareTo).reversed();

        responseList.sort(loanComparator);

        return responseList;
    }

    /**
     * Save new application to database basing on information about client.
     *
     * @param newClient client that requested new application.
     * @return id of saved application.
     */
    private Long saveNewApplicationToDatabase(ClientEntity newClient) {
        log.trace("Generating new application of client #{} to database.", newClient.getId());

        ApplicationEntity newApplication = createNewApplication(newClient);

        log.trace("Saving application '{}' to database.", newApplication);
        return applicationRepository.save(newApplication).getId();
    }

    /**
     * Save new client entity based on given loan application request information.
     *
     * @param loanApplicationRequestDTO information about request.
     * @return saved {@link ClientEntity}.
     */
    private ClientEntity saveNewClientToDatabase(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        ClientEntity newClient = LoanRequestMapper.INSTANCE.loanRequestToClient(loanApplicationRequestDTO);
        log.trace("New client '{}' is generated from application request.", newClient.toString());
        return clientRepository.save(newClient);
    }

}
