package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.players.PlayerResponse;
import com.thinkific.sportsapi.api.exception.PlayerIdDoesNotExistsException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractMatchCase {
    protected final GetPlayerSetCase playerSetCase;

    protected AbstractMatchCase(GetPlayerSetCase playerSetCase) {
        this.playerSetCase = playerSetCase;
    }

    protected final Set<PlayerResponse> checkPlayerResponse(Set<String> players, String teamId) {
        if(Objects.isNull(players) || players.isEmpty()){
            return Set.of();
        }

        final Set<PlayerResponse> playerResponses = playerSetCase.handle(players, teamId);

        if(players.size() == playerResponses.size()){
            return playerResponses;
        }

        final List<String> notFound = players
                .stream()
                .filter(item -> !playerResponses.contains(item))
                .toList();

        throw new PlayerIdDoesNotExistsException(notFound);
    }
}
