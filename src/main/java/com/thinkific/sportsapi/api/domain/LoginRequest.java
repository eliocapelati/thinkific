package com.thinkific.sportsapi.api.domain;

import javax.validation.constraints.NotEmpty;

public record LoginRequest(@NotEmpty String userNameOrEmail, @NotEmpty String password) { }
