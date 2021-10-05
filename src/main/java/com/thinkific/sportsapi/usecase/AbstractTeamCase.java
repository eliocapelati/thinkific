package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.exception.AlreadyExistsException;
import com.thinkific.sportsapi.data.domain.TeamEntity;
import com.thinkific.sportsapi.mapper.TeamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Objects;

public abstract class AbstractTeamCase<T extends MongoRepository<TeamEntity, String>> {

    protected static final String RESOURCE = "team";
    protected static final String VALUE = "teamName";

    @Autowired
    protected T repository;

    @Autowired
    protected TeamMapper mapper;

    @Autowired
    protected GetUserCase userCase;

    protected final void checkTeamExist(String teamName) {
        if (Objects.isNull(teamName) || teamName.isBlank()) {
            return;
        }
        final TeamEntity team = new TeamEntity();
        team.setTeamName(teamName);
        final Example<TeamEntity> of = Example.of(team);

        if (this.repository.exists(of)) {
            throw new AlreadyExistsException(RESOURCE, VALUE);
        }
    }

}
