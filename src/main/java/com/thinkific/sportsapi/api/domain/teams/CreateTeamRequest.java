package com.thinkific.sportsapi.api.domain.teams;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public record CreateTeamRequest(@NotEmpty @Size(min = 2) String teamName,
                                @NotEmpty String description) {
}
