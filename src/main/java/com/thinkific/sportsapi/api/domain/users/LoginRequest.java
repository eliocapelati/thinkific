package com.thinkific.sportsapi.api.domain.users;

import javax.validation.constraints.NotEmpty;

public record LoginRequest(@NotEmpty String userNameOrEmail, @NotEmpty String password) {
}
