package com.thinkific.sportsapi.mapper;

import com.thinkific.sportsapi.api.domain.PageableResponse;
import com.thinkific.sportsapi.api.domain.matches.CreateMatchRequest;
import com.thinkific.sportsapi.api.domain.matches.MatchResponse;
import com.thinkific.sportsapi.api.domain.matches.MatchSelector;
import com.thinkific.sportsapi.api.domain.matches.PatchMatchRequest;
import com.thinkific.sportsapi.api.domain.players.CreatePlayerRequest;
import com.thinkific.sportsapi.api.domain.players.PlayerResponse;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.data.domain.MatchesEntity;
import com.thinkific.sportsapi.data.domain.PlayersEntity;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface MatchMapper {

    @Mapping(target = "players", source = "players")
    @Mapping(target = "details", source = "request.detail", qualifiedByName = "addToList")
    @Mapping(target = "teamId", source = "teamResponse.id")
    @Mapping(target = "id", ignore = true)
    MatchesEntity from(CreateMatchRequest request, Set<PlayerResponse> players, TeamResponse teamResponse);

    @Mapping(target = "matchSelector", source = "date", qualifiedByName = "determineSelector")
    MatchResponse from(MatchesEntity entity);

    default PageableResponse<MatchResponse> from(Page<MatchesEntity> teamEntities) {
        if (Objects.isNull(teamEntities)) {
            return null;
        }

        final List<MatchResponse> collect = teamEntities
                .getContent()
                .stream()
                .map(this::from)
                .toList();

        return new PageableResponse<>(collect, teamEntities.getTotalPages(), teamEntities.getTotalElements());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget MatchesEntity entity, PatchMatchRequest patch);


    @Named("addToList")
    default List<String> addToList(String detail){
        if (detail == null) {
            return null;
        }
        return List.of(detail);
    }


    @Named("determineSelector")
    default MatchSelector determineSelector(LocalDateTime date){
        if (date == null) {
            return null;
        }
        return MatchSelector.get(date);
    }
}
