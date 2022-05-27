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

@Entity
@Getter
@Setter
@Table(name = "clients")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class ClientEntity {
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "martial_status")
    private MartialStatus maritalStatus;

    @Column(name = "dependent_amount")
    private Integer dependentAmount;

    @Type(type = "jsonb")
    @Column(name = "passport", columnDefinition = "jsonb")
    private PassportRecord passport;

    @Type(type = "jsonb")
    @Column(name = "employment", columnDefinition = "jsonb")
    private EmploymentRecord employment;

    @Column(name = "account")
    private Long account;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_id_generator")
    @SequenceGenerator(name = "client_id_generator", sequenceName = "clients_sequence", allocationSize = 1)
    private Long id;

    @SuppressWarnings("unused")
    public enum Gender {
        MALE, FEMALE, NON_BINARY
    }

    @SuppressWarnings("unused")
    public enum MartialStatus {
        MARRIED, DIVORCED, SINGLE, WIDOW_WIDOWER
    }
}


