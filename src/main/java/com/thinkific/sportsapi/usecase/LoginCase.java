package com.thinkific.sportsapi.usecase;


import com.thinkific.sportsapi.api.domain.LoginRequest;
import com.thinkific.sportsapi.api.domain.LoginResponse;
import com.thinkific.sportsapi.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class LoginCase {
    private final Logger log = LoggerFactory.getLogger(LoginCase.class);

    private final AuthService authService;

    public LoginCase(final AuthService authService) {
        this.authService = authService;
    }

    public LoginResponse handle(LoginRequest login){
        final LoginResponse holder = authService.login(login);
        log.debug("User successfully logged in with {} the token will expire in {}s",
                login.userNameOrEmail(), holder.getExpiresIn()
        );
        return holder;
    }
}
