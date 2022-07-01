package ru.leonov.deal.model.entity

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import ru.leonov.deal.model.enums.ClientGenderEnum
import ru.leonov.deal.model.enums.ClientMartialStatusEnum
import ru.leonov.deal.model.record.EmploymentRecord
import ru.leonov.deal.model.record.PassportRecord
import java.time.LocalDate
import javax.persistence.*

/**
 * Entity that represents client as object that stored in table "clients" in database.
 */
@Entity
@Table(name = "clients")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
data class ClientEntity(
    /**
     * First name of client.
     */
    @Column(name = "first_name", nullable = false)
    var firstName: String,
    /**
     * Last name of client.
     */
    @Column(name = "last_name", nullable = false)
    var lastName: String,
    /**
     * Middle name of client. Nullable.
     */
    @Column(name = "middle_name")
    var middleName: String?,
    /**
     * Client's birthdate.
     */
    @Column(name = "birth_date", nullable = false)
    var birthDate: LocalDate,
    /**
     * Client's e-mail address.
     */
    @Column(name = "email", nullable = false)
    var email: String,
    /**
     * Client's gender. Nullable.
     *
     * @see ClientGenderEnum
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    var gender: ClientGenderEnum? = null,
    /**
     * Client's martial status. Nullable.
     *
     * @see ClientMartialStatusEnum
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "martial_status")
    var maritalStatus: ClientMartialStatusEnum? = null,
    /**
     * Number of dependent persons (children etc.). Nullable.
     */
    @Column(name = "dependent_amount")
    var dependentAmount: Int? = null,
    /**
     * Information about client's passport. Nullable.
     *
     * @see PassportRecord
     */
    @Type(type = "jsonb")
    @Column(name = "passport", columnDefinition = "jsonb")
    var passport: PassportRecord,
    /**
     * Information about client's job. Nullable.
     *
     * @see EmploymentRecord
     */
    @Type(type = "jsonb")
    @Column(name = "employment", columnDefinition = "jsonb")
    var employment: EmploymentRecord? = null,
    /**
     * Client's bank account number. Nullable.
     */
    @Column(name = "account")
    var account: Long? = null,
    /**
     * Client's id in database. Primary key, autoincrement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_id_generator")
    @SequenceGenerator(name = "client_id_generator", sequenceName = "clients_sequence", allocationSize = 1)
    var id: Long? = null
)
