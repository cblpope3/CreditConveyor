package ru.leonov.deal.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.leonov.deal.model.entity.ApplicationEntity;

@Repository
public interface ApplicationRepository extends CrudRepository<ApplicationEntity, Long> {
}
