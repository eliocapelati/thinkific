package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.players.CreatePlayerRequest;
import com.thinkific.sportsapi.api.domain.players.PlayerResponse;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.data.domain.PlayersEntity;
import com.thinkific.sportsapi.data.repository.PlayersRepository;
import com.thinkific.sportsapi.mapper.PlayerMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class CreatePlayerCase {

    private final GetTeamCase getTeamCase;
    private final PlayersRepository repository;
    private final PlayerMapper mapper;

    public CreatePlayerCase(GetTeamCase getTeamCase,
                            PlayersRepository repository,
                            PlayerMapper mapper) {

        this.getTeamCase = getTeamCase;
        this.repository = repository;
        this.mapper = mapper;
    }

    public PlayerResponse handle(String teamId, String userEmail, CreatePlayerRequest request) {
        final TeamResponse handle = this.getTeamCase.handle(userEmail, teamId);

        final PlayersEntity players = this.mapper.from(request, handle);

        final PlayersEntity saved = this.repository.save(players);

        return mapper.from(saved);
    }
}
