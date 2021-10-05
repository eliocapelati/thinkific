package com.thinkific.sportsapi.api.domain.players;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record PlayerResponse(
        String id,
        String firstName,
        String lastName,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,
        String teamId) {
}
