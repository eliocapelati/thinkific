package com.thinkific.sportsapi.api.domain.matches;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.Set;

public record PatchMatchRequest(
        String opponent,
        String location,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime date,
        Set<String> addPlayers,
        Set<String> removePlayers) { }
