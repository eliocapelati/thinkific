package com.thinkific.sportsapi.service;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.CreatedUser;
import com.auth0.net.AuthRequest;
import com.auth0.net.SignUpRequest;
import com.thinkific.sportsapi.api.domain.CreateUserRequest;
import com.thinkific.sportsapi.api.domain.LoginRequest;
import com.thinkific.sportsapi.api.domain.LoginResponse;
import com.thinkific.sportsapi.api.exception.CantSignupUserException;
import com.thinkific.sportsapi.api.exception.UnauthorizedException;
import com.thinkific.sportsapi.config.properties.Auth0Properties;
import com.thinkific.sportsapi.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class Auth0ServiceImpl implements AuthService {
    private final Logger log = LoggerFactory.getLogger(Auth0ServiceImpl.class);

    private AuthAPI auth;
    private Auth0Properties properties;
    private UserMapper mapper;

    @Autowired
    public Auth0ServiceImpl(final AuthAPI auth,
                            final Auth0Properties properties,
                            final UserMapper mapper) {
        this.auth = auth;
        this.properties = properties;
        this.mapper = mapper;
    }

    @Override
    public void createUser(CreateUserRequest request){
        SignUpRequest signup = auth
                .signUp(request.getEmail(),
                        request.getUserName(),
                        request.getPassword().toCharArray(),
                        properties.getConnection()
                );

        try {
            CreatedUser user = signup.execute();
            log.debug("User successfully signed up [{}]", user);
        } catch (Auth0Exception exception) {
            log.error("Error: can not signup user [{}] {}", request.getEmail(), exception);
            throw new CantSignupUserException();
        }
    }

    @Override
    public LoginResponse login(LoginRequest login) {
        AuthRequest request = auth
                .login(login.userNameOrEmail(), login.password().toCharArray(), properties.getConnection())
                .setAudience(properties.getAudience())
                .setScope(properties.getScope());
        try {
            return mapper.from(request.execute());
        } catch (Auth0Exception exception) {
            log.debug("Auth0Exception {}", exception);
            throw new UnauthorizedException();
        }


    }
}
