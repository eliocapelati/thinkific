package com.thinkific.sportsapi.usecase;

import com.thinkific.sportsapi.api.domain.Pageable;
import com.thinkific.sportsapi.api.domain.PageableResponse;
import com.thinkific.sportsapi.api.domain.matches.MatchResponse;
import com.thinkific.sportsapi.api.domain.matches.MatchSelector;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.data.domain.MatchesEntity;
import com.thinkific.sportsapi.data.repository.MatchesRepository;
import com.thinkific.sportsapi.mapper.MatchMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Lazy
public class GetPageableMatchCase {
    private final GetTeamCase teamCase;
    private final MatchesRepository repository;
    private final MatchMapper mapper;

    public GetPageableMatchCase(GetTeamCase teamCase, MatchesRepository repository, MatchMapper mapper) {
        this.teamCase = teamCase;
        this.repository = repository;
        this.mapper = mapper;
    }

    public PageableResponse<MatchResponse> handle(String userEmail, String teamId, MatchSelector selector, Pageable page){
        final var pageRequest = PageRequest.of(page.getPage(), page.getSize());

        final var teamResponse = this.teamCase.handle(userEmail, teamId);
        final var allPaged = getAllByTeam(selector, teamResponse.id(), pageRequest);

        return mapper.from(allPaged);
    }

    private Page<MatchesEntity> getAllByTeam(MatchSelector selector, String teamId, PageRequest pageRequest){
        if (Objects.isNull(selector)) {
            return repository.findAllByTeamId(teamId, pageRequest);
        }
        return switch (selector) {
            case FUTURE -> repository.findAllByTeamIdAndDateAfter(teamId, pageRequest, LocalDateTime.now());
            case PAST -> repository.findAllByTeamIdAndDateBefore(teamId, pageRequest, LocalDateTime.now());
        };
    }
}
