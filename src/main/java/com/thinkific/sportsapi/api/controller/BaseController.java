package com.thinkific.sportsapi.api.controller;

import org.springframework.security.oauth2.jwt.Jwt;

public sealed class BaseController permits UsersController, TeamsController {
    protected String getUserEmail(Jwt principal){
        return principal.getClaimAsString("email");
    }
}
