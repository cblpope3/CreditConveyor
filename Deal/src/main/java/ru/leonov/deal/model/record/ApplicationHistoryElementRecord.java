package ru.leonov.deal.model.record;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.leonov.deal.model.entity.ApplicationEntity;

import java.time.LocalDate;

/**
 * POJO that represent element of credit application history.
 */
@Data
@Getter
@Setter
public class ApplicationHistoryElementRecord {

    /**
     * New status of credit application.
     */
    private ApplicationEntity.Status status;

    /**
     * Date of status changing.
     */
    private LocalDate date;
}
