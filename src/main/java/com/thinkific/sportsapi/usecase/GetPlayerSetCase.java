package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.players.PlayerResponse;
import com.thinkific.sportsapi.data.domain.PlayersEntity;
import com.thinkific.sportsapi.data.repository.PlayersRepository;
import com.thinkific.sportsapi.mapper.PlayerMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Lazy
public class GetPlayerSetCase {

    private final PlayersRepository repository;
    private final PlayerMapper mapper;

    public GetPlayerSetCase(PlayersRepository repository, PlayerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Set<PlayerResponse> handle(Set<String> playersId, String teamId){

        final List<PlayersEntity> players = this.repository.findAllByIdInAndTeamId(playersId, teamId);

        return players
                .stream()
                .map(mapper::from)
                .collect(Collectors.toSet());
    }
}
