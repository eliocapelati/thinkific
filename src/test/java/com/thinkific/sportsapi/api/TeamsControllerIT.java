package com.thinkific.sportsapi.api;


import com.thinkific.sportsapi.api.domain.PageableResponse;
import com.thinkific.sportsapi.api.domain.teams.CreateTeamRequest;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.api.exception.domain.ErrorInfo;
import com.thinkific.sportsapi.config.CommonSetup;
import com.thinkific.sportsapi.config.IntegrationTests;
import com.thinkific.sportsapi.data.domain.TeamEntity;
import com.thinkific.sportsapi.data.repository.TeamRepository;

import static org.junit.jupiter.api.Assertions.*;

import com.thinkific.sportsapi.data.repository.UsersRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import static org.springframework.http.MediaType.APPLICATION_JSON;


@IntegrationTests
@Testcontainers
class TeamsControllerIT extends CommonSetup<TeamRepository> {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.2");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    private final Logger log = LoggerFactory.getLogger(TeamsControllerIT.class);
    private final String BASE_PATH = "/v1/teams";
    @Autowired
    private UsersRepository usersRepository;


    @BeforeEach
    void run(){
        usersRepository.deleteAll();
        createValidUser();
    }

    @Test
    @DisplayName("Attempt to create a team Unauthorized")
    void attemptToCreateUnauthorized()  {

        client.post().uri(BASE_PATH)
                .contentType(APPLICATION_JSON)
                .bodyValue("{}")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorInfo.class)
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Attempt to create a empty team")
    void attemptToCreateEmptyTeam() {

        final var exchange =
                client
                    .post()
                    .uri(BASE_PATH)
                    .contentType(APPLICATION_JSON)
                    .bodyValue("{}")
                    .accept(APPLICATION_JSON)
                    .header("Authorization", getAccessToken())
                .exchange();

        exchange
                .expectStatus().isBadRequest()
                .expectBody(ErrorInfo.class)
                .value(errorInfo -> assertEquals(2, errorInfo.getErrors().size()))
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Create a team")
    void testCreateTeam() {

        final CreateTeamRequest request = new CreateTeamRequest(faker.esports().team(), faker.leagueOfLegends().quote());

        final var exchange =
                client
                        .post()
                        .uri(BASE_PATH)
                        .contentType(APPLICATION_JSON)
                        .bodyValue(request)
                        .accept(APPLICATION_JSON)
                        .header("Authorization", getAccessToken())
                        .exchange();

        exchange
                .expectStatus().isCreated()
                .expectBody(TeamResponse.class)
                .value(Assertions::assertNotNull)
                .value(teamResponse -> Assertions.assertNotNull(teamResponse.id()))
                .value(teamResponse -> Assertions.assertEquals(request.teamName(), teamResponse.teamName()))
                .value(teamResponse -> Assertions.assertEquals(request.description(), teamResponse.description()))
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Attempt to create with same name")
    void attemptToDuplicateATeam() {

        final CreateTeamRequest request = new CreateTeamRequest(faker.esports().team(), faker.leagueOfLegends().quote());

        final var exchange =
                client
                        .post()
                        .uri(BASE_PATH)
                        .contentType(APPLICATION_JSON)
                        .bodyValue(request)
                        .accept(APPLICATION_JSON)
                        .header("Authorization", getAccessToken())
                        .exchange();

        exchange
                .expectStatus().isCreated()
                .expectHeader().value("Location", header -> Assertions.assertTrue(header.contains("/v1/teams/")))
                .expectBody(TeamResponse.class)
                .value(Assertions::assertNotNull)
                .value(teamResponse -> Assertions.assertNotNull(teamResponse.id()))
                .value(teamResponse -> Assertions.assertEquals(request.teamName(), teamResponse.teamName()))
                .value(teamResponse -> Assertions.assertEquals(request.description(), teamResponse.description()))
                .consumeWith(consume -> log.debug("{}", consume));


        client
                .post()
                .uri(BASE_PATH)
                .contentType(APPLICATION_JSON)
                .bodyValue(request)
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorInfo.class)
                .value(errorInfo -> Assertions.assertEquals("Theres a team with this teamName", errorInfo.getMessage()))
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("List all teams created")
    void listTeams(){

        final int TEAMS_AMOUNT = 10;

        createTeams(TEAMS_AMOUNT);

        client
                .get()
                .uri(BASE_PATH)
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableResponse.class)
                .value(pageableResponse -> Assertions.assertEquals(TEAMS_AMOUNT, pageableResponse.getSize()))
                .consumeWith(consume -> log.debug("{}", consume) );

    }


    @Test
    @DisplayName("List no teams")
    void noTeams(){

        client
                .get()
                .uri(BASE_PATH)
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableResponse.class)
                .value(pageableResponse -> Assertions.assertEquals(0, pageableResponse.getSize()))
                .consumeWith(consume -> log.debug("{}", consume) );

    }

    @Test
    @DisplayName("Get team by id")
    void getTeamById(){

        final TeamResponse team = createTeam();

        client
                .get()
                .uri(BASE_PATH + "/{id}", team.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody(TeamResponse.class)
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Attempt to find a team by id")
    void attemptToFindTeamById(){


        client
                .get()
                .uri(BASE_PATH + "/{id}", 123)
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorInfo.class)
                .consumeWith(consume -> log.debug("{}", consume) );
    }


    @Test
    @DisplayName("Patch team by id")
    void patchTeamById(){

        final TeamResponse team = createTeam();
        final CreateTeamRequest patch = new CreateTeamRequest(faker.starTrek().character(), faker.hitchhikersGuideToTheGalaxy().quote());

        client
                .patch()
                .uri(BASE_PATH + "/{id}", team.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .bodyValue(patch)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(consume -> log.debug("{}", consume));

        final TeamEntity teamUpdated = this
                .repository
                .findById(team.id())
                .orElseThrow(() -> new RuntimeException("Should return a team"));


        Assertions.assertEquals(team.id(), teamUpdated.getId());
        Assertions.assertEquals(patch.teamName(), teamUpdated.getTeamName());
        Assertions.assertEquals(patch.description(), teamUpdated.getDescription());

    }

    @Test
    @DisplayName("Patch team with same name from other team")
    void patchSameNameOtherTeam(){

        final TeamResponse team1 = createTeam();
        final TeamResponse team2 = createTeam();
        final CreateTeamRequest patch = new CreateTeamRequest(team2.teamName(), faker.hitchhikersGuideToTheGalaxy().quote());

        client
                .patch()
                .uri(BASE_PATH + "/{id}", team1.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .bodyValue(patch)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorInfo.class)
                .value(errorInfo -> Assertions.assertEquals("Theres a team with this teamName", errorInfo.getMessage()))
                .consumeWith(consume -> log.debug("{}", consume));

    }

    @Test
    @DisplayName("Attempt to update a unknown team")
    void attemptToUpdateAnUnknownTeam(){

        createTeam();
        final CreateTeamRequest patch = new CreateTeamRequest(null, faker.hitchhikersGuideToTheGalaxy().quote());

        client
                .patch()
                .uri(BASE_PATH + "/{id}", "123")
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .bodyValue(patch)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorInfo.class)
                .consumeWith(consume -> log.debug("{}", consume));

    }
}
