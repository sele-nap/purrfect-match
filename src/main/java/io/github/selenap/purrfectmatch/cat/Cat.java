package io.github.selenap.purrfectmatch.cat;

import java.time.Instant;
import java.time.LocalDate;

import io.github.selenap.purrfectmatch.common.error.BusinessRuleException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "cat")
public class Cat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Sex sex;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CatStatus status = CatStatus.AVAILABLE;

    /** Optimistic lock: two concurrent approvals cannot reserve the same cat. */
    @Version
    private long version;

    @CreationTimestamp
    @Column(name = "arrived_at", nullable = false, updatable = false)
    private Instant arrivedAt;

    protected Cat() {
        // required by JPA
    }

    public Cat(String name, Sex sex, LocalDate birthDate, String description) {
        applyProfile(name, sex, birthDate, description);
    }

    public void updateProfile(String name, Sex sex, LocalDate birthDate, String description) {
        applyProfile(name, sex, birthDate, description);
    }

    // private, so it is safe to call from the constructor (Hibernate proxies subclass this entity)
    private void applyProfile(String name, Sex sex, LocalDate birthDate, String description) {
        this.name = name;
        this.sex = sex;
        this.birthDate = birthDate;
        this.description = description;
    }

    public void reserve() {
        if (status != CatStatus.AVAILABLE) {
            throw new BusinessRuleException(name + " is not available (status: " + status + ")");
        }
        status = CatStatus.RESERVED;
    }

    public void releaseReservation() {
        if (status == CatStatus.RESERVED) {
            status = CatStatus.AVAILABLE;
        }
    }

    public void markAdopted() {
        if (status != CatStatus.RESERVED) {
            throw new BusinessRuleException(name + " must be reserved before being adopted");
        }
        status = CatStatus.ADOPTED;
    }

    public boolean isAdopted() {
        return status == CatStatus.ADOPTED;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Sex getSex() { return sex; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getDescription() { return description; }
    public CatStatus getStatus() { return status; }
    public Instant getArrivedAt() { return arrivedAt; }
    public long getVersion() { return version; }
}
