package ru.leonov.deal.model.entity

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import ru.leonov.deal.dto.LoanOfferDTO
import ru.leonov.deal.model.enums.ApplicationStatusEnum
import ru.leonov.deal.model.record.ApplicationHistoryElementRecord
import java.time.LocalDate
import javax.persistence.*

/**
 * Entity that represents credit application process as object that stored in table "applications" in database.
 */
@Entity
@Table(name = "applications")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
data class ApplicationEntity(
    /**
     * Client that requested this credit application.
     *
     * @see ClientEntity
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "client", nullable = false)
    var client: ClientEntity,
    /**
     * Information about credit.
     *
     * @see CreditEntity
     */
    @OneToOne
    @JoinColumn(name = "credit")
    var credit: CreditEntity? = null,
    /**
     * Current application status.
     *
     * @see ApplicationStatusEnum
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ApplicationStatusEnum,
    /**
     * Date when this application was created.
     */
    @Column(name = "creation_date", nullable = false)
    val creationDate: LocalDate,
    /**
     * Credit offer that had been applied by client.
     */
    @Type(type = "jsonb")
    @Column(name = "applied_offer", columnDefinition = "jsonb")
    var appliedOffer: LoanOfferDTO? = null,
    /**
     * Date when this application had been signed.
     */
    @Column(name = "sign_date")
    var signDate: LocalDate? = null,
    /**
     * History of application status changing. Represented as [List] of status changes.
     *
     * @see ApplicationHistoryElementRecord
     */
    @Type(type = "jsonb")
    @Column(name = "status_history", columnDefinition = "jsonb")
    var statusHistory: MutableList<ApplicationHistoryElementRecord>,
    /**
     * Simple electronic sign code.
     */
    @Column(name = "ses_code")
    var sesCode: Int? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_id_generator")
    @SequenceGenerator(name = "application_id_generator", sequenceName = "applications_sequence", allocationSize = 1)
    var id: Long? = null

)
