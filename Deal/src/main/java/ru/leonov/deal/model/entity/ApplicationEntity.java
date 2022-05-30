package ru.leonov.deal.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import ru.leonov.deal.dto.LoanOfferDTO;
import ru.leonov.deal.model.record.ApplicationHistoryElementRecord;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Entity that represents credit application process as object that stored in table "applications" in database.
 */
@Getter
@Setter
@Entity
@Table(name = "applications")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ApplicationEntity {

    /**
     * Client that requested this credit application.
     *
     * @see ClientEntity
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "client", nullable = false)
    private ClientEntity client;

    /**
     * Information about credit.
     *
     * @see CreditEntity
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "credit")
    private CreditEntity credit;

    /**
     * Current application status.
     *
     * @see Status
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    /**
     * Date when this application was created.
     */
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    /**
     * Credit offer that had been applied by client.
     */
    @Type(type = "jsonb")
    @Column(name = "applied_offer", columnDefinition = "jsonb")
    private LoanOfferDTO appliedOffer;

    /**
     * Date when this application had been signed.
     */
    @Column(name = "sign_date")
    private LocalDate signDate;

    /**
     * History of application status changing. Represented as {@link List} of status changes.
     *
     * @see ApplicationHistoryElementRecord
     */
    @Type(type = "jsonb")
    @Column(name = "status_history", columnDefinition = "jsonb")
    private List<ApplicationHistoryElementRecord> statusHistory;

    /**
     * Simple electronic sign code.
     */
    @Column(name = "ses_code")
    private Integer sesCode;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_id_generator")
    @SequenceGenerator(name = "application_id_generator", sequenceName = "applications_sequence", allocationSize = 1)
    private Long id;

    /**
     * Credit application status enum. Possible values are: 'PREAPPROVAL', 'APPROVED', 'CC_DENIED', 'CC_APPROVED',
     * 'PREPARE_DOCUMENTS', 'DOCUMENT_CREATED', 'CLIENT_DENIED', 'DOCUMENT_SIGNED', 'CREDIT_ISSUED',
     */
    @SuppressWarnings("unused")
    public enum Status {
        /**
         * Application is pre-approved.
         */
        PREAPPROVAL,
        /**
         * Application is approved.
         */
        APPROVED,
        /**
         * Application is denied by credit-conveyor.
         */
        CC_DENIED,
        /**
         * Application is approved by credit-conveyor.
         */
        CC_APPROVED,
        /**
         * Documents for application are preparing.
         */
        PREPARE_DOCUMENTS,
        /**
         * Documents for application had been created.
         */
        DOCUMENT_CREATED,
        /**
         * Application is denied by client.
         */
        CLIENT_DENIED,
        /**
         * Documents for application are signed.
         */
        DOCUMENT_SIGNED,
        /**
         * Credit is issued.
         */
        CREDIT_ISSUED,
    }

}
