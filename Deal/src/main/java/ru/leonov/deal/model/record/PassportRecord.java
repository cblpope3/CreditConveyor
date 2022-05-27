package ru.leonov.deal.model.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassportRecord {
    private Integer series;
    private Integer number;
    private LocalDate issueDate;
    private String issueBranch;
}
