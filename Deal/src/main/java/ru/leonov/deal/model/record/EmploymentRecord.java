package ru.leonov.deal.model.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * POJO that contain information about client's employment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentRecord {

    /**
     * Client's employment status.
     *
     * @see EmploymentStatus
     */
    private EmploymentStatus employmentStatus;

    /**
     * Client's employer organization name.
     */
    private String employer;

    /**
     * Current client's salary.
     */
    private BigDecimal salary;

    /**
     * Clients job position.
     *
     * @see Position
     */
    private Position position;

    /**
     * Client's total work experience in months.
     */
    private Integer workExperienceTotal;

    /**
     * Client's work experience in current position in months.
     */
    private Integer workExperienceCurrent;

    /**
     * Client's employment status enum. Possible values are: 'UNEMPLOYED', 'SELF_EMPLOYED', 'EMPLOYED', 'BUSINESS_OWNER'.
     */
    @SuppressWarnings("unused")
    public enum EmploymentStatus {
        UNEMPLOYED, SELF_EMPLOYED, EMPLOYED, BUSINESS_OWNER
    }

    /**
     * Client's employment position enum. Possible values are: 'WORKER', 'MID_MANAGER', 'TOP_MANAGER', 'OWNER'.
     */
    @SuppressWarnings("unused")
    public enum Position {
        WORKER, MID_MANAGER, TOP_MANAGER, OWNER
    }
}

