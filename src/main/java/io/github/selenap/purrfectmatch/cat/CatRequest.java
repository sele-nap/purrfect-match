package io.github.selenap.purrfectmatch.cat;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public record CatRequest(
        @NotBlank @Size(max = 60) String name,
        @NotNull Sex sex,
        @PastOrPresent LocalDate birthDate,
        @Size(max = 2000) String description) {
}
