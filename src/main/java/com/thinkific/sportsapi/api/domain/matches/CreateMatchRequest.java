package com.thinkific.sportsapi.api.domain.matches;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

public record CreateMatchRequest(
        @NotEmpty String opponent,
        @NotEmpty String location,
        @Future @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime date,
        @NotEmpty String detail,
        @NotEmpty Set<@NotBlank String> players
) {
}
