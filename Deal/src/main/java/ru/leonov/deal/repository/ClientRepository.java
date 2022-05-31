package ru.leonov.deal.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.leonov.deal.model.entity.ClientEntity;

@Repository
public interface ClientRepository extends CrudRepository<ClientEntity, Long> {
}
