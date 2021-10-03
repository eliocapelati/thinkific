package com.thinkific.sportsapi.api;

import com.github.javafaker.Faker;
import com.thinkific.sportsapi.api.domain.LoginRequest;
import com.thinkific.sportsapi.api.domain.LoginResponse;
import com.thinkific.sportsapi.api.domain.UserResponse;
import com.thinkific.sportsapi.config.ContainerSetup;
import com.thinkific.sportsapi.config.IntegrationTests;
import com.thinkific.sportsapi.api.domain.CreateUserRequest;
import com.thinkific.sportsapi.api.exception.domain.ErrorInfo;
import com.thinkific.sportsapi.config.JwtTestSecurityConfig;
import com.thinkific.sportsapi.data.repository.UsersRepository;
import com.thinkific.sportsapi.service.AuthService;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@IntegrationTests
class UsersControllerIT implements ContainerSetup {

    private final Logger log = LoggerFactory.getLogger(UsersControllerIT.class);
    private final String BASE_PATH = "/v1/users";

    @Autowired
    private WebTestClient client;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private AuthService auth;
    @Autowired
    private Faker faker;

    @BeforeEach
    public void setup(){
        usersRepository.deleteAll();
        client = client.mutate().responseTimeout(Duration.ofSeconds(60)).build();
    }

    @Test
    @DisplayName("Attempt to create an empty user")
    void attemptToCreateEmptyUser()  {
        client.post().uri(BASE_PATH)
                .contentType(APPLICATION_JSON)
                .bodyValue("{}")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError();
    }


    @Test
    @DisplayName("Attempt to create an user with Invalid password")
    void attemptToCreatUserWithInvalidPassword() {

        final var userRequest = createUserRequest("1234");

        final var exchange = client.post()
                .uri(BASE_PATH)
                .contentType(APPLICATION_JSON)
                .bodyValue(userRequest)
                .accept(APPLICATION_JSON)
                .exchange();

        exchange
                .expectStatus().is4xxClientError()
                .expectBody(ErrorInfo.class)
                .value(errorInfo -> assertEquals(1, errorInfo.getErrors().size()))
                .value(errorInfo -> assertEquals("createUserRequest", errorInfo.getMessage()))
                .consumeWith(out::println);

    }

    @Test
    @DisplayName("Successful create an user")
    void successFullCreateUser() {
        final var password = getValidPassword();
        var userRequest = createUserRequest(password);

        client.post()
                .uri(BASE_PATH)
                .contentType(APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    @DisplayName("Attempt duplicate a user")
    void attemptDuplicateUser() {
        final var strongPassword = getValidPassword();
        final var userRequest = createUserRequest(strongPassword);

        doNothing().when(auth).createUser(any());

        client.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().is2xxSuccessful();

        client.post()
                .uri(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorInfo.class)
                .value(errorInfo -> assertEquals("Theres a user with this email", errorInfo.getMessage()))
                .consumeWith(out::println);
    }

    @Test
    @DisplayName("Returns Bad Request username on Login")
    void badUserName() {
        var login = """
                    {
                        "password" : "%s"
                    }
                    """.formatted(getValidPassword());

        //client = client.mutate().responseTimeout(Duration.ofMinutes(15)).build();


        final var response = client
                .post()
                .uri(BASE_PATH + "/login")
                .contentType(APPLICATION_JSON)
                .bodyValue(login)
                .exchange();

        response
            .expectStatus().is4xxClientError()
            .expectBody(ErrorInfo.class)
            .value(errorInfo -> assertEquals(1, errorInfo.getErrors().size()));
    }

    @Test
    @DisplayName("Returns Bad Request password on Login")
    void badPassword() {
        var login = """
                    {
                        "userNameOrEmail" : "%s"
                    }
                    """.formatted(faker.internet().emailAddress());


        final var response = client.post()
                .uri(BASE_PATH + "/login")
                .contentType(APPLICATION_JSON)
                .bodyValue(login)
                .exchange();

        response
                .expectStatus().is4xxClientError()
                .expectBody(ErrorInfo.class)
                .value(errorInfo -> assertEquals(1, errorInfo.getErrors().size()));
    }

    @Test
    @DisplayName("Success Login user")
    void loginUser() {
        var loginRequest = new LoginRequest(faker.internet().emailAddress(), getValidPassword());


        when(auth.login(any())).thenReturn(createLoginResponse());

        final var response = client.post()
                .uri(BASE_PATH + "/login")
                .contentType(APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange();

        response
            .expectStatus().is2xxSuccessful()
            .expectBody(LoginResponse.class)
            .value(Assertions::assertNotNull)
            .value(loginResponse -> assertNotNull(loginResponse.getAccessToken()))
            .consumeWith(consume -> log.debug("LoginResponse {}", consume));

    }

    @Test
    @DisplayName("Unauthorized Get user info")
    void getUnauthorizedUserInfo() {

        final var response = client.get()
                .uri(BASE_PATH)
                .accept(APPLICATION_JSON)
                .exchange();

        response
                .expectStatus().isUnauthorized()
                .expectBody()
                .consumeWith(consume -> log.debug("Unauthorized {}", consume));
    }

    @Test
    @DisplayName("Get user info")
    void getUserInfo() {

        final var password = getValidPassword();
        var userRequest = createUserRequest(password, JwtTestSecurityConfig.USER_EMAIL_VALUE);


        client.post()
                .uri(BASE_PATH)
                .contentType(APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().is2xxSuccessful();

        var loginResponse = createLoginResponse();

        final String token = "%s %s".formatted(loginResponse.getTokenType(), loginResponse.getAccessToken());

        final var response = client.get()
                .uri(BASE_PATH)
                .accept(APPLICATION_JSON)
                .header("Authorization", token)
                .exchange();

        response
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .value(Assertions::assertNotNull)
                .value(item -> assertEquals(item.lastName(), userRequest.getLastName()))
                .value(item -> assertEquals(item.firstName(), userRequest.getFirstName()))
                .value(item -> assertEquals(item.email(), userRequest.getEmail()))
                .value(item -> assertNotNull(item.id()))
                .consumeWith(consume -> log.debug("Success {}", consume));

    }

    private String getValidPassword() {
        return faker.regexify("([0-9][a-z][A-Z][!@#$~&*()+=:/?\"*]){8,100}");
    }

    private CreateUserRequest createUserRequest(final String password, final String email){
        var userRequest = new CreateUserRequest();
        userRequest.setPassword(password);
        userRequest.setUserName(faker.regexify("[a-z]{5,15}"));
        userRequest.setLastName(faker.name().lastName());
        userRequest.setEmail(email);
        userRequest.setFirstName(faker.name().firstName());
        return userRequest;
    }

    private CreateUserRequest createUserRequest(final String password) {
        return createUserRequest(password, faker.internet().emailAddress());
    }

    private LoginResponse createLoginResponse(){
        final var response = new LoginResponse();

        response.setTokenType("Bearer");
        response.setAccessToken(faker.internet().uuid());
        response.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        response.setRefreshToken(faker.internet().uuid());
        response.setExpiresIn(86400L);
        response.setScope("openid profile email address phone update:current_user_metadata");

        return response;
    }


}
