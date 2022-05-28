package ru.leonov.deal.repository;

import org.springframework.data.repository.CrudRepository;
import ru.leonov.deal.model.entity.ApplicationEntity;

import java.util.List;

public interface ApplicationRepository extends CrudRepository<ApplicationEntity, Long> {
    List<ApplicationEntity> findAll();
}
