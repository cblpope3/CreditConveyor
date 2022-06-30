package ru.leonov.deal.mapper

import ru.leonov.deal.dto.LoanApplicationRequestDTO
import ru.leonov.deal.model.entity.ClientEntity
import ru.leonov.deal.model.record.PassportRecord

/**
 * Create new incomplete [ClientEntity] from [LoanApplicationRequestDTO].
 */
fun loanRequestToClient(loanApplicationRequestDTO: LoanApplicationRequestDTO): ClientEntity {
    val passportRecord = PassportRecord(
        series = loanApplicationRequestDTO.passportSeries.toInt(),
        number = loanApplicationRequestDTO.passportNumber.toInt()
    )
    return ClientEntity(
        firstName = loanApplicationRequestDTO.firstName,
        lastName = loanApplicationRequestDTO.lastName,
        middleName = loanApplicationRequestDTO.middleName,
        birthDate = loanApplicationRequestDTO.birthdate,
        email = loanApplicationRequestDTO.email,
        passport = passportRecord
    )
}
