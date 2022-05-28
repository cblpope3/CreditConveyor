package ru.leonov.deal.model.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * POJO that contain information about client's passport.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassportRecord {

    /**
     * Client's passport series.
     */
    private Integer series;

    /**
     * Client's passport number.
     */
    private Integer number;

    /**
     * Client's passport issue date.
     */
    private LocalDate issueDate;

    /**
     * Name of client's passport issue organization.
     */
    private String issueBranch;
}
