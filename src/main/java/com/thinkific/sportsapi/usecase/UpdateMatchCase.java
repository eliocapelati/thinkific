package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.matches.MatchSelector;
import com.thinkific.sportsapi.api.domain.matches.PatchMatchRequest;
import com.thinkific.sportsapi.api.domain.players.PlayerResponse;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.api.exception.InvalidPatchOnPastMatch;
import com.thinkific.sportsapi.api.exception.NotFoundException;
import com.thinkific.sportsapi.data.domain.MatchesEntity;
import com.thinkific.sportsapi.data.repository.MatchesRepository;
import com.thinkific.sportsapi.mapper.MatchMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Set;

@Lazy
@Service
public class UpdateMatchCase extends AbstractMatchCase {

    private static final String RESOURCE = "Match";
    private final Logger log = LoggerFactory.getLogger(UpdateMatchCase.class);

    private final GetTeamCase teamCase;
    private final MatchesRepository repository;
    private final MatchMapper mapper;

    public UpdateMatchCase(GetTeamCase teamCase, MatchesRepository repository,
                           MatchMapper mapper, GetPlayerSetCase getPlayerSetCase) {

        super(getPlayerSetCase);
        this.teamCase = teamCase;
        this.repository = repository;
        this.mapper = mapper;
    }

    public void handle(PatchMatchRequest request, String teamId, String userEmail, String matchId){
        final TeamResponse teamResponse = teamCase.handle(userEmail, teamId);

        final MatchesEntity matchesEntity = this.repository
                .findByIdAndTeamId(matchId, teamResponse.id())
                .orElseThrow(() -> new NotFoundException(RESOURCE));

        if(MatchSelector.PAST.equals(MatchSelector.get(matchesEntity.getDate()))){
            throw new InvalidPatchOnPastMatch();
        }

        final Set<PlayerResponse> addPlayers = checkPlayerResponse(request.addPlayers(), teamResponse.id());
        final Set<PlayerResponse> removePlayers = checkPlayerResponse(request.removePlayers(), teamResponse.id());

        mapper.update(matchesEntity, request);

        if(!addPlayers.isEmpty()){
            matchesEntity.getPlayers().addAll(addPlayers);
        }
        if(!removePlayers.isEmpty()){
            matchesEntity.getPlayers().removeAll(removePlayers);
        }

        log.trace("Patched the match id {}", matchId);

        repository.save(matchesEntity);

        log.debug("Updated the match id {}", matchId);

    }
}
