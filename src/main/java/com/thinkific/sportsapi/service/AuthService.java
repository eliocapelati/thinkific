package com.thinkific.sportsapi.service;


import com.thinkific.sportsapi.api.domain.users.CreateUserRequest;
import com.thinkific.sportsapi.api.domain.users.LoginRequest;
import com.thinkific.sportsapi.api.domain.users.LoginResponse;

public interface AuthService {
    void createUser(CreateUserRequest request);

    LoginResponse login(LoginRequest login);
}
