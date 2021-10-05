package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.Pageable;
import com.thinkific.sportsapi.api.domain.PageableResponse;
import com.thinkific.sportsapi.api.domain.players.PlayerResponse;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.data.domain.PlayersEntity;
import com.thinkific.sportsapi.data.repository.PlayersRepository;
import com.thinkific.sportsapi.mapper.PlayerMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class GetPageablePlayerCase {

    private final PlayersRepository repository;
    private final GetTeamCase teamCase;
    private final PlayerMapper mapper;

    public GetPageablePlayerCase(PlayersRepository repository, GetTeamCase teamCase, PlayerMapper mapper) {
        this.repository = repository;
        this.teamCase = teamCase;
        this.mapper = mapper;
    }

    public PageableResponse<PlayerResponse> handle(String userEmail, String teamId, Pageable page){
        final var pageRequest = PageRequest.of(page.getPage(), page.getSize());

        final TeamResponse teamResponse = this.teamCase.handle(userEmail, teamId);

        final Page<PlayersEntity> allPaged = this.repository.findAllByTeamId(teamResponse.id(), pageRequest);

        return mapper.from(allPaged);
    }
}
