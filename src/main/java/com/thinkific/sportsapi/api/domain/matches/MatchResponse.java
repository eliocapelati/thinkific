package com.thinkific.sportsapi.api.domain.matches;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.thinkific.sportsapi.api.domain.players.PlayerResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record MatchResponse(
        String id,
        String opponent,
        String location,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        LocalDateTime date,
        List<String> details,
        Set<PlayerResponse> players,
        MatchSelector matchSelector
) {
}
