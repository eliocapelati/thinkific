package com.thinkific.sportsapi.api;

import com.thinkific.sportsapi.api.domain.users.LoginRequest;
import com.thinkific.sportsapi.api.domain.users.LoginResponse;
import com.thinkific.sportsapi.api.domain.users.UserResponse;
import com.thinkific.sportsapi.config.CommonSetup;
import com.thinkific.sportsapi.config.IntegrationTests;
import com.thinkific.sportsapi.api.exception.domain.ErrorInfo;
import com.thinkific.sportsapi.config.JwtTestSecurityConfig;
import com.thinkific.sportsapi.data.repository.UsersRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@IntegrationTests
@Testcontainers
class UsersControllerIT extends CommonSetup<UsersRepository> {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.2");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    private final Logger log = LoggerFactory.getLogger(UsersControllerIT.class);
    private final String BASE_PATH = "/v1/users";



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
                .expectStatus().isCreated();
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
    @DisplayName("Returns Bad Request on password with invalid json structure")
    void badRequestInvalidJsonFormat() {
        var login = """
                    {
                        "password" : "98""
                        "userNameOrEmail" : "98"
                    }
                    """;

        final var response = client
                .post()
                .uri(BASE_PATH + "/login")
                .contentType(APPLICATION_JSON)
                .bodyValue(login)
                .exchange();

        response
                .expectStatus().is4xxClientError()
                .expectBody(ErrorInfo.class)
                .consumeWith(consume -> log.debug("ErrorInfo {}", consume));
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

        createValidUser(userRequest);

        final var response = client.get()
                .uri(BASE_PATH)
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
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

}
