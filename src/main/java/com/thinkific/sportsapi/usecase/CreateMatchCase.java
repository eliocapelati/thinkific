package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.matches.CreateMatchRequest;
import com.thinkific.sportsapi.api.domain.matches.MatchResponse;
import com.thinkific.sportsapi.api.domain.players.PlayerResponse;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.data.domain.MatchesEntity;
import com.thinkific.sportsapi.data.repository.MatchesRepository;
import com.thinkific.sportsapi.mapper.MatchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


import java.util.Set;

@Service
@Lazy
public class CreateMatchCase extends AbstractMatchCase {
    private final Logger log = LoggerFactory.getLogger(CreateMatchCase.class);

    private final GetTeamCase getTeamCase;
    private final MatchMapper mapper;
    private final MatchesRepository repository;


    public CreateMatchCase(GetTeamCase getTeamCase, GetPlayerSetCase playerSetCase, MatchMapper mapper, MatchesRepository repository) {
        super(playerSetCase);
        this.getTeamCase = getTeamCase;
        this.mapper = mapper;
        this.repository = repository;
    }

    public MatchResponse handle(String userEmail, String teamId, CreateMatchRequest request){
        final TeamResponse teamResponse = this.getTeamCase.handle(userEmail, teamId);
        final Set<PlayerResponse> playerResponses = checkPlayerResponse(request.players(), teamResponse.id());

        final MatchesEntity matchesEntity = mapper.from(request, playerResponses, teamResponse);
        log.debug("Mapped MatchesEntity {}", matchesEntity);

        final MatchesEntity save = this.repository.save(matchesEntity);
        log.debug("Successfully created Match {} for Team {}", save.getId(), teamId);

        return mapper.from(save);
    }


}
