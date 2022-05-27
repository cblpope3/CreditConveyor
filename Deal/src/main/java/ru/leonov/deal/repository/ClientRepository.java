package ru.leonov.deal.repository;

import org.springframework.data.repository.CrudRepository;
import ru.leonov.deal.model.entity.ClientEntity;

import java.util.List;

public interface ClientRepository extends CrudRepository<ClientEntity, Long> {
    List<ClientEntity> findAll();
}
