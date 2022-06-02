package ru.leonov.deal.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import ru.leonov.deal.model.record.EmploymentRecord;
import ru.leonov.deal.model.record.PassportRecord;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity that represents client as object that stored in table "clients" in database.
 */
@Entity
@Getter
@Setter
@Table(name = "clients")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ClientEntity {

    /**
     * First name of client.
     */
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /**
     * Last name of client.
     */
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /**
     * Middle name of client. Nullable.
     */
    @Column(name = "middle_name")
    private String middleName;

    /**
     * Client's birthdate.
     */
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    /**
     * Client's e-mail address.
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Client's gender. Nullable.
     *
     * @see Gender
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    /**
     * Client's martial status. Nullable.
     *
     * @see MartialStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "martial_status")
    private MartialStatus maritalStatus;

    /**
     * Number of dependent persons (children etc.). Nullable.
     */
    @Column(name = "dependent_amount")
    private Integer dependentAmount;

    /**
     * Information about client's passport. Nullable.
     *
     * @see PassportRecord
     */
    @Type(type = "jsonb")
    @Column(name = "passport", columnDefinition = "jsonb")
    private PassportRecord passport;

    /**
     * Information about client's job. Nullable.
     *
     * @see EmploymentRecord
     */
    @Type(type = "jsonb")
    @Column(name = "employment", columnDefinition = "jsonb")
    private EmploymentRecord employment;

    /**
     * Client's bank account number. Nullable.
     */
    @Column(name = "account")
    private Long account;

    /**
     * Client's id in database. Primary key, autoincrement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_id_generator")
    @SequenceGenerator(name = "client_id_generator", sequenceName = "clients_sequence", allocationSize = 1)
    private Long id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientEntity client)) return false;
        return firstName.equals(client.firstName) && lastName.equals(client.lastName) && Objects.equals(middleName, client.middleName) && birthDate.equals(client.birthDate) && email.equals(client.email) && gender == client.gender && maritalStatus == client.maritalStatus && Objects.equals(dependentAmount, client.dependentAmount) && Objects.equals(passport, client.passport) && Objects.equals(employment, client.employment) && Objects.equals(account, client.account) && Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, middleName, birthDate, email, gender, maritalStatus, dependentAmount, passport, employment, account, id);
    }

    /**
     * Client's gender enum. Possible values are: 'MALE', 'FEMALE' or 'NON_BINARY'.
     */
    @SuppressWarnings("unused")
    public enum Gender {
        MALE, FEMALE, NON_BINARY
    }

    /**
     * Client's martial status enum. Possible values are: 'MARRIED', 'DIVORCED', 'SINGLE', 'WIDOW_WIDOWER'.
     */
    @SuppressWarnings("unused")
    public enum MartialStatus {
        MARRIED, DIVORCED, SINGLE, WIDOW_WIDOWER
    }
}


