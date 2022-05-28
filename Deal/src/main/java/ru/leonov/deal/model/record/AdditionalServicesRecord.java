package ru.leonov.deal.model.record;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO that contain information about additional services applied to client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalServicesRecord {

    /**
     * Did client purchase insurance?
     */
    private Boolean isInsuranceEnabled;

    /**
     * Is this client salary client?
     */
    private Boolean isSalaryClient;
}
