package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.players.CreatePlayerRequest;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.api.exception.NotFoundException;
import com.thinkific.sportsapi.data.domain.PlayersEntity;
import com.thinkific.sportsapi.data.repository.PlayersRepository;
import com.thinkific.sportsapi.mapper.PlayerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class UpdatePlayerCase {
    private static final String RESOURCE = "Player";
    private final Logger log = LoggerFactory.getLogger(UpdatePlayerCase.class);

    private final PlayersRepository repository;
    private final GetTeamCase teamCase;
    private final PlayerMapper mapper;

    public UpdatePlayerCase(PlayersRepository repository, GetTeamCase teamCase, PlayerMapper mapper) {
        this.repository = repository;
        this.teamCase = teamCase;
        this.mapper = mapper;
    }

    public void handle(String userEmail, String playerId, String teamId, CreatePlayerRequest patch){
        final TeamResponse teamResponse = teamCase.handle(userEmail, teamId);

        final PlayersEntity playersEntity = this.repository
                .findByIdAndTeamId(playerId, teamResponse.id())
                .orElseThrow(() -> new NotFoundException(RESOURCE));

        mapper.update(playersEntity, patch);

        log.trace("Patched the player id {}", playerId);

        repository.save(playersEntity);

        log.debug("Updated the player id {}", playerId);
    }
}
