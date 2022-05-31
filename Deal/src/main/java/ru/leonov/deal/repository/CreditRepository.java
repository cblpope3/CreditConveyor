package ru.leonov.deal.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.leonov.deal.model.entity.CreditEntity;

@Repository
public interface CreditRepository extends CrudRepository<CreditEntity, Long> {
}
