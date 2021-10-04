package com.thinkific.sportsapi.usecase;


import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.api.exception.NotFoundException;
import com.thinkific.sportsapi.data.domain.TeamEntity;
import com.thinkific.sportsapi.data.repository.TeamRepository;
import com.thinkific.sportsapi.mapper.TeamMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class GetTeamCase {
    private static final String RESOURCE = "Team";
    private final TeamRepository repository;
    private final TeamMapper mapper;
    private final GetUserCase userCase;

    public GetTeamCase(TeamRepository repository, TeamMapper mapper, GetUserCase userCase) {
        this.repository = repository;
        this.mapper = mapper;
        this.userCase = userCase;
    }

    public TeamResponse handle(String userEmail, String teamId){
        final TeamEntity teamEntity = new TeamEntity(teamId);
        teamEntity.setUserId(this.userCase.handle(userEmail).id());

        final Example<TeamEntity> of = Example.of(teamEntity);

        return this.repository
                   .findOne(of)
                   .map(mapper::from)
                   .orElseThrow(() -> new NotFoundException(RESOURCE));
    }
}
