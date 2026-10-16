package io.github.selenap.purrfectmatch.adoption;

import java.time.Instant;

public record AdoptionRequestResponse(
        Long id,
        Long catId,
        String catName,
        String applicantName,
        String applicantEmail,
        String message,
        AdoptionStatus status,
        Instant createdAt,
        Instant decidedAt) {

    static AdoptionRequestResponse from(AdoptionRequest r) {
        return new AdoptionRequestResponse(r.getId(), r.getCat().getId(), r.getCat().getName(),
                r.getApplicantName(), r.getApplicantEmail(), r.getMessage(), r.getStatus(),
                r.getCreatedAt(), r.getDecidedAt());
    }
}
