package ru.leonov.deal.repository;

import org.springframework.data.repository.CrudRepository;
import ru.leonov.deal.model.entity.CreditEntity;

import java.util.List;

public interface CreditRepository extends CrudRepository<CreditEntity, Long> {
    List<CreditEntity> findAll();
}
