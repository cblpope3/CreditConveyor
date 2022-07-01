package ru.leonov.deal.mapper

import ru.leonov.deal.dto.CreditDTO
import ru.leonov.deal.dto.EmploymentDTO
import ru.leonov.deal.dto.FinishRegistrationRequestDTO
import ru.leonov.deal.dto.ScoringDataDTO
import ru.leonov.deal.model.entity.ApplicationEntity
import ru.leonov.deal.model.entity.ClientEntity
import ru.leonov.deal.model.entity.CreditEntity
import ru.leonov.deal.model.enums.ClientGenderEnum
import ru.leonov.deal.model.enums.ClientMartialStatusEnum
import ru.leonov.deal.model.enums.EmploymentPositionEnum
import ru.leonov.deal.model.enums.EmploymentStatusEnum
import ru.leonov.deal.model.record.AdditionalServicesRecord
import ru.leonov.deal.model.record.EmploymentRecord
import ru.leonov.deal.model.record.PassportRecord

/**
 * Create new [ScoringDataDTO] object from [ApplicationEntity] data.
 * @throws IllegalArgumentException if required data field in [ApplicationEntity] is null.
 */
fun mapApplicationEntityToScoringDataDTO(application: ApplicationEntity): ScoringDataDTO {

    val employmentRecord = application.client.employment
        ?: throw IllegalArgumentException("Can't map scoring data: employment is null!")

    val employmentDto = EmploymentDTO(
        employmentStatus = EmploymentDTO.EmploymentStatus.valueOf(employmentRecord.employmentStatus.name),
        employerINN = employmentRecord.employer,
        salary = employmentRecord.salary,
        position = EmploymentDTO.Position.valueOf(employmentRecord.position.name),
        workExperienceTotal = employmentRecord.workExperienceTotal,
        workExperienceCurrent = employmentRecord.workExperienceCurrent,
    )

    val loanOfferDTO = application.appliedOffer
        ?: throw IllegalArgumentException("Can't map scoring data: appliedOffer is null!")

    return ScoringDataDTO(
        amount = loanOfferDTO.requestedAmount,
        term = loanOfferDTO.term,
        isInsuranceEnabled = loanOfferDTO.isInsuranceEnabled,
        isSalaryClient = loanOfferDTO.isSalaryClient,
        firstName = application.client.firstName,
        lastName = application.client.lastName,
        middleName = application.client.middleName,
        birthdate = application.client.birthDate,
        passportSeries = application.client.passport.series.toString(),
        passportNumber = application.client.passport.number.toString(),
        gender = ScoringDataDTO.Gender.valueOf(
            application.client.gender?.name
                ?: throw IllegalArgumentException("Can't map scoring data: gender is null!")
        ),
        maritalStatus = ScoringDataDTO.MaritalStatus.valueOf(
            application.client.maritalStatus?.name
                ?: throw IllegalArgumentException("Can't map scoring data: martialStatus is null!")
        ),
        dependentAmount = application.client.dependentAmount
            ?: throw IllegalArgumentException("Can't map scoring data: dependentAmount is null!"),
        passportIssueDate = application.client.passport.issueDate
            ?: throw IllegalArgumentException("Can't map scoring data: passportIssueDate is null!"),
        passportIssueBranch = application.client.passport.issueBranch
            ?: throw IllegalArgumentException("Can't map scoring data: passportIssueBranch is null!"),
        account = application.client.account?.toString()
            ?: throw IllegalArgumentException("Can't map scoring data: account is null!"),
        employment = employmentDto
    )
}

/**
 * Create new [CreditEntity] from given [CreditDTO].
 */
fun mapCreditDTOtoCreditEntity(creditDTO: CreditDTO): CreditEntity {

    val additionalServicesRecord = AdditionalServicesRecord(
        isInsuranceEnabled = creditDTO.isInsuranceEnabled,
        isSalaryClient = creditDTO.isSalaryClient
    )

    return CreditEntity(
        amount = creditDTO.amount,
        term = creditDTO.term,
        monthlyPayment = creditDTO.monthlyPayment,
        rate = creditDTO.rate,
        psk = creditDTO.psk,
        paymentSchedule = creditDTO.paymentSchedule,
        additionalServices = additionalServicesRecord
    )
}

/**
 * Create new completed [ClientEntity] based on incomplete [ClientEntity] and [FinishRegistrationRequestDTO].
 */
fun mapFinishRegistrationToClient(registrationRequest: FinishRegistrationRequestDTO, client: ClientEntity):
        ClientEntity {

    val passportRecord = PassportRecord(
        series = client.passport.series,
        number = client.passport.number,
        issueDate = registrationRequest.passportIssueDate,
        issueBranch = registrationRequest.passportIssueBranch
    )

    val employmentRecord = EmploymentRecord(
        employmentStatus = EmploymentStatusEnum.valueOf(registrationRequest.employment.employmentStatus.value),
        employer = registrationRequest.employment.employerINN,
        salary = registrationRequest.employment.salary,
        position = EmploymentPositionEnum.valueOf(registrationRequest.employment.position.value),
        workExperienceTotal = registrationRequest.employment.workExperienceTotal,
        workExperienceCurrent = registrationRequest.employment.workExperienceCurrent
    )

    return ClientEntity(

        firstName = client.firstName,
        lastName = client.lastName,
        middleName = client.middleName,
        birthDate = client.birthDate,
        email = client.email,
        gender = ClientGenderEnum.valueOf(registrationRequest.gender.value),
        id = client.id,

        passport = passportRecord,
        employment = employmentRecord,
        maritalStatus = ClientMartialStatusEnum.valueOf(registrationRequest.maritalStatus.value),
        dependentAmount = registrationRequest.dependentAmount,
        account = registrationRequest.account.toLong()
    )
}
