package io.github.selenap.purrfectmatch.adoption;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdoptionRequestCreate(
        @NotBlank @Size(max = 120) String applicantName,
        @NotBlank @Email @Size(max = 254) String applicantEmail,
        @Size(max = 2000) String message) {
}
