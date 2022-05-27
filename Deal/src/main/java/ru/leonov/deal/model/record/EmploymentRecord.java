package ru.leonov.deal.model.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentRecord {

    private EmploymentStatus employmentStatus;
    private String employer;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;

    @SuppressWarnings("unused")
    public enum EmploymentStatus {
        UNEMPLOYED, SELF_EMPLOYED, EMPLOYED, BUSINESS_OWNER
    }

    @SuppressWarnings("unused")
    public enum Position {
        WORKER, MID_MANAGER, TOP_MANAGER, OWNER
    }
}

