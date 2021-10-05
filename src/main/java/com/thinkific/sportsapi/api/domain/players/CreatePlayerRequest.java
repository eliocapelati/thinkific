package com.thinkific.sportsapi.api.domain.players;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public record CreatePlayerRequest(
        @NotEmpty @Size(min = 2) String firstName,
        @NotEmpty @Size(min = 2) String lastName,
        @Past @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthDate) {
}
