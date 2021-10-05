package com.thinkific.sportsapi.api;

import com.thinkific.sportsapi.api.domain.PageableResponse;
import com.thinkific.sportsapi.api.domain.players.CreatePlayerRequest;
import com.thinkific.sportsapi.api.domain.players.PlayerResponse;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.api.exception.domain.ErrorInfo;
import com.thinkific.sportsapi.config.CommonSetup;
import com.thinkific.sportsapi.config.IntegrationTests;
import com.thinkific.sportsapi.data.repository.PlayersRepository;
import com.thinkific.sportsapi.data.repository.UsersRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@IntegrationTests
@Testcontainers
class PlayersControllerIT extends CommonSetup<PlayersRepository> {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.2");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    private final Logger log = LoggerFactory.getLogger(TeamsControllerIT.class);
    private final String BASE_PATH = "/v1/teams/{teamsId}/players";
    @Autowired
    private UsersRepository usersRepository;


    @BeforeEach
    void run(){
        usersRepository.deleteAll();
        createValidUser();
    }

    @Test
    @DisplayName("Attempt to create a player Unauthorized")
    void attemptToCreateUnauthorized()  {

        final TeamResponse team = createTeam();

        client.post()
                .uri(BASE_PATH, team.id())
                .contentType(APPLICATION_JSON)
                .bodyValue("{}")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorInfo.class)
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Attempt to create a empty player")
    void attemptToCreateEmpty()  {

        final TeamResponse team = createTeam();

        client.post()
                .uri(BASE_PATH, team.id())
                .contentType(APPLICATION_JSON)
                .bodyValue("{}")
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorInfo.class)
                .value(errorInfo -> assertEquals(3, errorInfo.getErrors().size()))
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Successful create a  player")
    void successfulCreatePlayer()  {

        final TeamResponse team = createTeam();
        final CreatePlayerRequest playerRequest = createPlayerRequest();

        client.post()
                .uri(BASE_PATH, team.id())
                .contentType(APPLICATION_JSON)
                .bodyValue(playerRequest)
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PlayerResponse.class)
                .value(playerResponse -> Assertions.assertNotNull(playerResponse.id()))
                .value(playerResponse -> Assertions.assertEquals(team.id(), playerResponse.teamId()))
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Successful delete a player")
    void successfulDeletePlayer()  {

        final TeamResponse team = createTeam();
        final CreatePlayerRequest playerRequest = createPlayerRequest();

        final var responseBodySpec = client.post()
                .uri(BASE_PATH, team.id())
                .contentType(APPLICATION_JSON)
                .bodyValue(playerRequest)
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PlayerResponse.class);

        responseBodySpec
                .value(playerResponse -> Assertions.assertNotNull(playerResponse.id()))
                .value(playerResponse -> Assertions.assertEquals(team.id(), playerResponse.teamId()))
                .consumeWith(consume -> log.debug("{}", consume));


        final var playerResponse = responseBodySpec
                    .returnResult()
                    .getResponseBody();

        Assertions.assertNotNull(playerResponse);

        client.delete()
                .uri(BASE_PATH + "/{playId}", team.id(), playerResponse.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .consumeWith(consume -> log.debug("{}", consume) );
    }



    @Test
    @DisplayName("Successful create 10 players and return it paged")
    void successfulCreateTenPlayers()  {

        final TeamResponse team = createTeam();

        createPlayers(10, team.id());

        client.get()
                .uri(BASE_PATH, team.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PageableResponse.class)
                .value(pageableResponse -> assertEquals(10, pageableResponse.getSize()))
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Successful create 100 players and return it paged")
    void successfulCreateHundredPlayers()  {

        final TeamResponse team = createTeam();

        createPlayers(100, team.id());

        client.get()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(BASE_PATH)
                                .queryParam("page", "0")
                                .queryParam("size", "10")
                                .build(team.id())
                )
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PageableResponse.class)
                .value(pageableResponse -> assertEquals(10, pageableResponse.getSize()))
                .value(pageableResponse -> assertEquals(100, pageableResponse.getTotalElements()))
                .value(pageableResponse -> assertEquals(10, pageableResponse.getTotalPages()))
                .consumeWith(consume -> log.debug("{}", consume) );
    }
}
