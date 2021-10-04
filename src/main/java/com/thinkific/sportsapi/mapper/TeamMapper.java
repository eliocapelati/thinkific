package com.thinkific.sportsapi.mapper;

import com.thinkific.sportsapi.api.domain.PageableResponse;
import com.thinkific.sportsapi.api.domain.teams.CreateTeamRequest;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.api.domain.users.UserResponse;
import com.thinkific.sportsapi.data.domain.TeamEntity;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "id", ignore = true)
    TeamEntity from(CreateTeamRequest request, UserResponse user);

    TeamResponse from(TeamEntity entity);

    default PageableResponse<TeamResponse> from(Page<TeamEntity> teamEntities){
        if(Objects.isNull(teamEntities)){
            return null;
        }

        final List<TeamResponse> collect = teamEntities
                .getContent()
                .stream()
                .map(this::from)
                .toList();

        return new PageableResponse<>(collect, teamEntities.getTotalPages(), teamEntities.getNumberOfElements());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTeam(@MappingTarget TeamEntity entity, CreateTeamRequest patch);

}
