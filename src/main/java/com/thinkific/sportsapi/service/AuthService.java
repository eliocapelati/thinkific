package com.thinkific.sportsapi.service;


import com.thinkific.sportsapi.api.domain.CreateUserRequest;
import com.thinkific.sportsapi.api.domain.LoginRequest;
import com.thinkific.sportsapi.api.domain.LoginResponse;

public interface AuthService {
    void createUser(CreateUserRequest request);

    LoginResponse login(LoginRequest login);
}
