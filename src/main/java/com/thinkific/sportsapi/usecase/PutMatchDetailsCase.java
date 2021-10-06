package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.matches.PutDetailMatchRequest;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.api.exception.NotFoundException;
import com.thinkific.sportsapi.data.domain.MatchesEntity;
import com.thinkific.sportsapi.data.repository.MatchesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class PutMatchDetailsCase {

    private final Logger log = LoggerFactory.getLogger(PutMatchDetailsCase.class);
    private static final String RESOURCE = "Match";
    private final GetTeamCase teamCase;
    private final MatchesRepository repository;


    public PutMatchDetailsCase(GetTeamCase teamCase, MatchesRepository repository) {
        this.teamCase = teamCase;
        this.repository = repository;
    }

    public void handle(PutDetailMatchRequest putDetail, String email, String teamId, String matchId){
        final TeamResponse teamResponse = teamCase.handle(email, teamId);

        final MatchesEntity matchesEntity = this.repository
                .findByIdAndTeamId(matchId, teamResponse.id())
                .orElseThrow(() -> new NotFoundException(RESOURCE));

        matchesEntity.setDetails(putDetail.detail());

        repository.save(matchesEntity);

        log.debug("Add detail to the match id {}", matchId);

    }
}
