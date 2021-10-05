package com.thinkific.sportsapi.mapper;

import com.thinkific.sportsapi.api.domain.PageableResponse;
import com.thinkific.sportsapi.api.domain.players.CreatePlayerRequest;
import com.thinkific.sportsapi.api.domain.players.PlayerResponse;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.data.domain.PlayersEntity;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "id", ignore = true)
    PlayersEntity from(CreatePlayerRequest request, TeamResponse team);

    PlayerResponse from(PlayersEntity entity);


    default PageableResponse<PlayerResponse> from(Page<PlayersEntity> teamEntities) {
        if (Objects.isNull(teamEntities)) {
            return null;
        }

        final List<PlayerResponse> collect = teamEntities
                .getContent()
                .stream()
                .map(this::from)
                .toList();
        System.out.println(teamEntities);

        return new PageableResponse<>(collect, teamEntities.getTotalPages(), teamEntities.getTotalElements());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTeam(@MappingTarget PlayersEntity entity, CreatePlayerRequest patch);

}
