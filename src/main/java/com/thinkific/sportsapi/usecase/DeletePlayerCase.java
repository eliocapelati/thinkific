package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.api.exception.NotFoundException;
import com.thinkific.sportsapi.data.domain.PlayersEntity;
import com.thinkific.sportsapi.data.repository.PlayersRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class DeletePlayerCase {
    private final static String RESOURCE = "Player";
    private final PlayersRepository repository;
    private final GetTeamCase teamCase;

    public DeletePlayerCase(PlayersRepository repository, GetTeamCase teamCase) {
        this.repository = repository;
        this.teamCase = teamCase;
    }

    public void handle(String userEmail, String playerId, String teamId){
        final TeamResponse teamResponse = teamCase.handle(userEmail, teamId);

        final PlayersEntity playersEntity = this.repository
                .findByIdAndTeamId(playerId, teamResponse.id())
                .orElseThrow(() -> new NotFoundException(RESOURCE));


        this.repository.delete(playersEntity);
    }

}
