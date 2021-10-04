package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.Pageable;
import com.thinkific.sportsapi.api.domain.PageableResponse;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.data.domain.TeamEntity;
import com.thinkific.sportsapi.data.repository.TeamRepository;
import com.thinkific.sportsapi.mapper.TeamMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class GetPageableTeamCase {

    private final TeamRepository repository;
    private final TeamMapper mapper;
    private final GetUserCase userCase;

    public GetPageableTeamCase(TeamRepository repository, TeamMapper mapper, GetUserCase userCase) {
        this.repository = repository;
        this.mapper = mapper;
        this.userCase = userCase;
    }

    public PageableResponse<TeamResponse> handle(String userEmail, Pageable page){
        final var pageRequest = PageRequest.of(page.page(), page.size());

        final var entityExample = new TeamEntity();
        entityExample.setUserId(userCase.handle(userEmail).id());
        final var example = Example.of(entityExample);

        final var all = this.repository.findAll(example, pageRequest);

        return mapper.from(all);
    }
}
