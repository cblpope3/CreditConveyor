package ru.leonov.deal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.leonov.deal.dto.CreditDTO;
import ru.leonov.deal.dto.FinishRegistrationRequestDTO;
import ru.leonov.deal.dto.ScoringDataDTO;
import ru.leonov.deal.model.entity.ApplicationEntity;
import ru.leonov.deal.model.entity.ClientEntity;
import ru.leonov.deal.model.entity.CreditEntity;

@Mapper
public interface FinishRegistrationMapper {

    FinishRegistrationMapper INSTANCE = Mappers.getMapper(FinishRegistrationMapper.class);

    @Mapping(target = "amount", source = "application.appliedOffer.requestedAmount")
    @Mapping(target = "term", source = "application.appliedOffer.term")
    @Mapping(target = "insuranceEnabled", source = "application.appliedOffer.insuranceEnabled")
    @Mapping(target = "salaryClient", source = "application.appliedOffer.salaryClient")

    @Mapping(target = "firstName", source = "application.client.firstName")
    @Mapping(target = "lastName", source = "application.client.lastName")
    @Mapping(target = "middleName", source = "application.client.middleName")
    @Mapping(target = "birthdate", source = "application.client.birthDate")
    @Mapping(target = "passportSeries", source = "application.client.passport.series")
    @Mapping(target = "passportNumber", source = "application.client.passport.number")
    @Mapping(target = "gender", source = "application.client.gender")
    @Mapping(target = "maritalStatus", source = "application.client.maritalStatus")
    @Mapping(target = "dependentAmount", source = "application.client.dependentAmount")
    @Mapping(target = "passportIssueDate", source = "application.client.passport.issueDate")
    @Mapping(target = "passportIssueBranch", source = "application.client.passport.issueBranch")

    @Mapping(target = "employment.employmentStatus", source = "application.client.employment.employmentStatus")
    @Mapping(target = "employment.employerINN", source = "application.client.employment.employer")
    @Mapping(target = "employment.salary", source = "application.client.employment.salary")
    @Mapping(target = "employment.position", source = "application.client.employment.position")
    @Mapping(target = "employment.workExperienceTotal", source = "application.client.employment.workExperienceTotal")
    @Mapping(target = "employment.workExperienceCurrent", source = "application.client.employment.workExperienceCurrent")

    @Mapping(target = "account", source = "application.client.account")
    ScoringDataDTO mapApplicationEntityToScoringData(ApplicationEntity application);


    @Mapping(target = "additionalServices.isInsuranceEnabled", source = "creditDTO.insuranceEnabled")
    @Mapping(target = "additionalServices.isSalaryClient", source = "creditDTO.salaryClient")
    CreditEntity mapCreditDTOtoCreditEntity(CreditDTO creditDTO);


    @Mapping(target = "passport.series", source = "client.passport.series")
    @Mapping(target = "passport.number", source = "client.passport.number")

    @Mapping(target = "gender", source = "registrationRequest.gender")
    @Mapping(target = "maritalStatus", source = "registrationRequest.maritalStatus")
    @Mapping(target = "dependentAmount", source = "registrationRequest.dependentAmount")
    @Mapping(target = "passport.issueDate", source = "registrationRequest.passportIssueDate")
    @Mapping(target = "passport.issueBranch", source = "registrationRequest.passportIssueBranch")
    @Mapping(target = "employment", source = "registrationRequest.employment")
    @Mapping(target = "employment.employer", source = "registrationRequest.employment.employerINN")
    @Mapping(target = "account", source = "registrationRequest.account")
    ClientEntity mapFinishRegistrationToClient(FinishRegistrationRequestDTO registrationRequest,
                                               ClientEntity client);

}
