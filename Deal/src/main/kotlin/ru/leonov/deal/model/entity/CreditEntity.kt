package ru.leonov.deal.model.entity

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import ru.leonov.deal.dto.PaymentScheduleElementDTO
import ru.leonov.deal.model.enums.CreditStatusEnum
import ru.leonov.deal.model.record.AdditionalServicesRecord
import java.math.BigDecimal
import javax.persistence.*

/**
 * Entity that represents credit as object that stored in "credits" database table.
 */
@Entity
@Table(name = "credits")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
data class CreditEntity(
    /**
     * Credit amount in roubles.
     */
    @Column(name = "amount", nullable = false)
    val amount: BigDecimal,
    /**
     * Credit term in months.
     */
    @Column(name = "term", nullable = false)
    val term: Int,
    /**
     * Credit monthly payment in roubles.
     */
    @Column(name = "monthly_payment", nullable = false)
    val monthlyPayment: BigDecimal,
    /**
     * Yearly credit rate in percents.
     */
    @Column(name = "rate", nullable = false)
    val rate: BigDecimal,
    /**
     * Credit full price in percents.
     */
    @Column(name = "psk", nullable = false)
    val psk: BigDecimal,
    /**
     * Payment schedule. Represented as [List] of payments.
     *
     * @see PaymentScheduleElementDTO
     */
    @Type(type = "jsonb")
    @Column(name = "payment_schedule", columnDefinition = "jsonb", nullable = false)
    val paymentSchedule: List<PaymentScheduleElementDTO>,
    /**
     * Additional services applied to client.
     *
     * @see AdditionalServicesRecord
     */
    @Type(type = "jsonb")
    @Column(name = "additional_services", columnDefinition = "jsonb", nullable = false)
    val additionalServices: AdditionalServicesRecord,
    /**
     * Current status of credit.
     *
     * @see CreditStatusEnum
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "credit_status", nullable = false)
    var creditStatus: CreditStatusEnum? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "credit_id_generator")
    @SequenceGenerator(name = "credit_id_generator", sequenceName = "credits_sequence", allocationSize = 1)
    var id: Long? = null
)
