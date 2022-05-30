package ru.leonov.deal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.leonov.deal.dto.LoanApplicationRequestDTO;
import ru.leonov.deal.model.entity.ClientEntity;

@Mapper
public interface LoanRequestMapper {

    LoanRequestMapper INSTANCE = Mappers.getMapper(LoanRequestMapper.class);

    @Mapping(source = "passportSeries", target = "passport.series")
    @Mapping(source = "passportNumber", target = "passport.number")
    @Mapping(source = "birthdate", target = "birthDate")
    ClientEntity loanRequestToClient(LoanApplicationRequestDTO loanApplicationRequestDTO);

}
