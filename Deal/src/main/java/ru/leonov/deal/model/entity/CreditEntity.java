package ru.leonov.deal.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import ru.leonov.deal.dto.PaymentScheduleElementDTO;
import ru.leonov.deal.model.record.AdditionalServicesRecord;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * Entity that represents credit as object that stored in "credits" database table.
 */
@Getter
@Setter
@Entity
@Table(name = "credits")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class CreditEntity {

    /**
     * Credit amount in roubles.
     */
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    /**
     * Credit term in months.
     */
    @Column(name = "term", nullable = false)
    private Integer term;

    /**
     * Credit monthly payment in roubles.
     */
    @Column(name = "monthly_payment", nullable = false)
    private BigDecimal monthlyPayment;

    /**
     * Yearly credit rate in percents.
     */
    @Column(name = "rate", nullable = false)
    private BigDecimal rate;

    /**
     * Credit full price in percents.
     */
    @Column(name = "psk", nullable = false)
    private BigDecimal psk;

    /**
     * Payment schedule. Represented as {@link List} of payments.
     *
     * @see PaymentScheduleElementDTO
     */
    @Type(type = "jsonb")
    @Column(name = "payment_schedule", columnDefinition = "jsonb", nullable = false)
    private List<PaymentScheduleElementDTO> paymentSchedule;

    /**
     * Additional services applied to client.
     *
     * @see AdditionalServicesRecord
     */
    @Type(type = "jsonb")
    @Column(name = "additional_services", columnDefinition = "jsonb", nullable = false)
    private AdditionalServicesRecord additionalServices;

    /**
     * Current status of credit.
     *
     * @see CreditStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "credit_status", nullable = false)
    private CreditStatus creditStatus;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credit_id_generator")
    @SequenceGenerator(name = "credit_id_generator", sequenceName = "credits_sequence", allocationSize = 1)
    private Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditEntity that = (CreditEntity) o;
        return amount.equals(that.amount) && term.equals(that.term) && Objects.equals(monthlyPayment, that.monthlyPayment) && Objects.equals(rate, that.rate) && Objects.equals(psk, that.psk) && Objects.equals(paymentSchedule, that.paymentSchedule) && additionalServices.equals(that.additionalServices) && creditStatus == that.creditStatus && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, term, monthlyPayment, rate, psk, paymentSchedule, additionalServices, creditStatus, id);
    }

    /**
     * Credit status enum. Possible values are: 'CALCULATED', 'ISSUED'.
     */
    @SuppressWarnings("unused")
    public enum CreditStatus {
        CALCULATED,
        ISSUED
    }
}
