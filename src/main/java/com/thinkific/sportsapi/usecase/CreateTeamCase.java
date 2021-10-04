package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.teams.CreateTeamRequest;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.api.domain.users.UserResponse;
import com.thinkific.sportsapi.api.exception.AlreadyExistsException;
import com.thinkific.sportsapi.data.domain.TeamEntity;
import com.thinkific.sportsapi.data.repository.TeamRepository;
import com.thinkific.sportsapi.mapper.TeamMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class CreateTeamCase extends AbstractTeamCase<TeamRepository> {
    private final TeamMapper mapper;
    private final GetUserCase userCase;

    public CreateTeamCase(TeamMapper mapper, GetUserCase userCase) {
        this.mapper = mapper;
        this.userCase = userCase;
    }

    public TeamResponse handle(CreateTeamRequest request, String userEmail){
        checkTeamExist(request.teamName());
        final UserResponse response = this.userCase.handle(userEmail);

        final TeamEntity createEntity = mapper.from(request, response);

        final TeamEntity team = this
                .repository
                .save(createEntity);

        return mapper.from(team);
    }

}
