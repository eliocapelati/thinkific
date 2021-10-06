package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.teams.CreateTeamRequest;
import com.thinkific.sportsapi.api.domain.users.UserResponse;
import com.thinkific.sportsapi.api.exception.NotFoundException;
import com.thinkific.sportsapi.data.domain.TeamEntity;
import com.thinkific.sportsapi.data.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;


@Lazy
@Service
public class UpdateTeamCase extends AbstractTeamCase<TeamRepository> {

    private final Logger log = LoggerFactory.getLogger(UpdateTeamCase.class);


    public void handle(final String email, final String teamId, final CreateTeamRequest patch) {
        checkTeamExist(patch.teamName());

        final UserResponse user = this.userCase.handle(email);

        final TeamEntity teamExample = new TeamEntity();
        teamExample.setUserId(user.id());
        teamExample.setId(teamId);

        final Example<TeamEntity> of = Example.of(teamExample);

        final TeamEntity teamEntity = this.repository
                .findOne(of)
                .orElseThrow(() -> new NotFoundException(RESOURCE));

        mapper.update(teamEntity, patch);

        this.repository.save(teamEntity);

        log.debug("Patched the team id {}", teamId);
    }

}
