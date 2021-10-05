package com.thinkific.sportsapi.api.controller;

import com.thinkific.sportsapi.api.domain.users.CreateUserRequest;
import com.thinkific.sportsapi.api.domain.users.LoginRequest;
import com.thinkific.sportsapi.api.domain.users.LoginResponse;
import com.thinkific.sportsapi.api.domain.users.UserResponse;
import com.thinkific.sportsapi.usecase.GetUserCase;
import com.thinkific.sportsapi.usecase.LoginCase;
import com.thinkific.sportsapi.usecase.SignupUserCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/v1/users")
public final class UsersController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(UsersController.class);
    private final SignupUserCase signupUserCase;
    private final LoginCase loginCase;
    private final GetUserCase userCase;

    public UsersController(final SignupUserCase signupUserCase,
                           final LoginCase loginCase,
                           final GetUserCase userCase) {
        this.signupUserCase = signupUserCase;
        this.loginCase = loginCase;
        this.userCase = userCase;
    }

    @PostMapping(path = {"", "/"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createUser(@Valid @RequestBody CreateUserRequest request) {
        log.trace("Create User Request {}", request);
        signupUserCase.handle(request);
    }

    @PostMapping(value = {"/login", "/login/"}, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    LoginResponse login(@Valid @RequestBody LoginRequest login) {
        log.trace("Login Request {}", login);
        return loginCase.handle(login);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    UserResponse getUserInfo(@AuthenticationPrincipal Jwt principal) {
        log.trace("Get Uer {}", principal);
        final String email = getUserEmail(principal);
        return userCase.handle(email);
    }

}
