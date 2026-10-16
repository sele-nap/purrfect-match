package io.github.selenap.purrfectmatch.cat;

import java.time.Instant;
import java.time.LocalDate;

public record CatResponse(
        Long id,
        String name,
        Sex sex,
        LocalDate birthDate,
        String description,
        CatStatus status,
        Instant arrivedAt) {

    public static CatResponse from(Cat cat) {
        return new CatResponse(cat.getId(), cat.getName(), cat.getSex(), cat.getBirthDate(),
                cat.getDescription(), cat.getStatus(), cat.getArrivedAt());
    }
}
