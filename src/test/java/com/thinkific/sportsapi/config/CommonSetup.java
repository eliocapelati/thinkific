package com.thinkific.sportsapi.config;

import com.github.javafaker.Faker;
import com.thinkific.sportsapi.api.domain.teams.CreateTeamRequest;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.api.domain.users.CreateUserRequest;
import com.thinkific.sportsapi.api.domain.users.LoginResponse;
import com.thinkific.sportsapi.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public abstract class CommonSetup<T extends MongoRepository<?,?>> {

    private final Logger log = LoggerFactory.getLogger(CommonSetup.class);

    @Autowired
    protected T repository;

    @Autowired
    protected WebTestClient client;

    @Autowired
    protected AuthService auth;

    @Autowired
    protected Faker faker;

    @BeforeEach
    public final void setup(){
        log.debug("Cleaning up!");
        repository.deleteAll();
        client = client.mutate().responseTimeout(Duration.ofMinutes(60)).build();
    }

    protected final String getValidPassword() {
        return faker.regexify("([0-9][a-z][A-Z][!@#]){8,100}");
    }

    protected final CreateUserRequest createUserRequest(final String password, final String email){
        var userRequest = new CreateUserRequest();
        userRequest.setPassword(password);
        userRequest.setUserName(faker.regexify("[a-z]{5,15}"));
        userRequest.setLastName(faker.name().lastName());
        userRequest.setEmail(email);
        userRequest.setFirstName(faker.name().firstName());
        return userRequest;
    }

    protected final CreateUserRequest createUserRequest(final String password) {
        return createUserRequest(password, faker.internet().emailAddress());
    }

    protected final LoginResponse createLoginResponse(){
        final var response = new LoginResponse();

        response.setTokenType("Bearer");
        response.setAccessToken(faker.internet().uuid());
        response.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        response.setRefreshToken(faker.internet().uuid());
        response.setExpiresIn(86400L);
        response.setScope("openid profile email address phone update:current_user_metadata");

        return response;
    }


    protected String getAccessToken(){
        var loginResponse = createLoginResponse();

        return "%s %s".formatted(loginResponse.getTokenType(), loginResponse.getAccessToken());
    }

    protected final void createValidUser(){
        final var password = getValidPassword();
        var userRequest = createUserRequest(password, JwtTestSecurityConfig.USER_EMAIL_VALUE);
        createValidUser(userRequest);
    }

    protected final void createValidUser(CreateUserRequest userRequest){
        client.post()
                .uri("/v1/users")
                .contentType(APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    protected TeamResponse createTeam(){
        final CreateTeamRequest request = new CreateTeamRequest(faker.regexify("[a-z]{5,15}"), faker.starTrek().character());
        log.debug("Creating a new team {} ", request);
        return client
                .post()
                .uri("/v1/teams")
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectBody(TeamResponse.class)
                .returnResult()
                .getResponseBody();
    }

    protected final void createTeams(Integer amount){
        for (int i = 0; i < amount; i++) {
            final TeamResponse team = createTeam();
            log.debug("Created the team {} ", team);
        }
    }
}
