package com.thinkific.sportsapi.api.domain.matches;

import javax.validation.constraints.NotEmpty;

public record PutDetailMatchRequest(@NotEmpty String detail) { }
