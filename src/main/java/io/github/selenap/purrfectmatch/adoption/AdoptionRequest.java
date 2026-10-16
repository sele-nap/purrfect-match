package io.github.selenap.purrfectmatch.adoption;

import java.time.Instant;

import io.github.selenap.purrfectmatch.cat.Cat;
import io.github.selenap.purrfectmatch.common.error.BusinessRuleException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Lifecycle: SUBMITTED → APPROVED → COMPLETED, or → REJECTED at any point before completion.
 * Each transition also updates the cat's status.
 */
@Entity
@Table(name = "adoption_request")
public class AdoptionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cat_id")
    private Cat cat;

    @Column(name = "applicant_name", nullable = false, length = 120)
    private String applicantName;

    @Column(name = "applicant_email", nullable = false, length = 254)
    private String applicantEmail;

    @Column(length = 2000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AdoptionStatus status = AdoptionStatus.SUBMITTED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "decided_at")
    private Instant decidedAt;

    protected AdoptionRequest() {
        // required by JPA
    }

    public AdoptionRequest(Cat cat, String applicantName, String applicantEmail, String message) {
        if (cat.isAdopted()) {
            throw new BusinessRuleException(cat.getName() + " has already been adopted");
        }
        this.cat = cat;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.message = message;
    }

    public void approve() {
        requireStatus(AdoptionStatus.SUBMITTED, "approve");
        cat.reserve();
        transitionTo(AdoptionStatus.APPROVED);
    }

    public void reject() {
        if (status == AdoptionStatus.APPROVED) {
            cat.releaseReservation();
        } else {
            requireStatus(AdoptionStatus.SUBMITTED, "reject");
        }
        transitionTo(AdoptionStatus.REJECTED);
    }

    public void complete() {
        requireStatus(AdoptionStatus.APPROVED, "complete");
        cat.markAdopted();
        transitionTo(AdoptionStatus.COMPLETED);
    }

    private void requireStatus(AdoptionStatus expected, String action) {
        if (status != expected) {
            throw new BusinessRuleException(
                    "Cannot " + action + " a request with status " + status);
        }
    }

    private void transitionTo(AdoptionStatus next) {
        status = next;
        decidedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Cat getCat() { return cat; }
    public String getApplicantName() { return applicantName; }
    public String getApplicantEmail() { return applicantEmail; }
    public String getMessage() { return message; }
    public AdoptionStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getDecidedAt() { return decidedAt; }
}
