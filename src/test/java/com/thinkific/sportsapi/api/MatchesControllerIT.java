package com.thinkific.sportsapi.api;

import com.thinkific.sportsapi.api.domain.PageableResponse;
import com.thinkific.sportsapi.api.domain.matches.*;
import com.thinkific.sportsapi.api.domain.players.PlayerResponse;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.api.exception.domain.ErrorInfo;
import com.thinkific.sportsapi.config.CommonSetup;
import com.thinkific.sportsapi.config.IntegrationTests;
import com.thinkific.sportsapi.data.domain.MatchesEntity;
import com.thinkific.sportsapi.data.repository.MatchesRepository;
import com.thinkific.sportsapi.data.repository.UsersRepository;
import com.thinkific.sportsapi.mapper.MatchMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@IntegrationTests
@Testcontainers
public class MatchesControllerIT extends CommonSetup<MatchesRepository> {

    private final Logger log = LoggerFactory.getLogger(MatchesControllerIT.class);
    private final String BASE_PATH = "/v1/teams/{teamsId}/matches";

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.2");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private MatchMapper mapper;
    private TeamResponse teamInstance;
    private List<PlayerResponse> playersInstance;

    @BeforeEach
    void run(){
        usersRepository.deleteAll();
        createValidUser();
        teamInstance = createTeam();
        playersInstance = createPlayers(10, teamInstance);
    }


    @Test
    @DisplayName("Attempt to create a match Unauthorized")
    void attemptToCreateUnauthorized()  {

        client.post()
                .uri(BASE_PATH, teamInstance.id())
                .contentType(APPLICATION_JSON)
                .bodyValue("{}")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorInfo.class)
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Attempt to create a empty match")
    void attemptToCreateEmpty()  {

        client.post()
                .uri(BASE_PATH, teamInstance.id())
                .contentType(APPLICATION_JSON)
                .bodyValue("{}")
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorInfo.class)
                .value(errorInfo -> assertEquals(5, errorInfo.getErrors().size()))
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Successful create a match")
    void successfulCreateMatch()  {
        var match = createMatchRequest();
        client.post()
                .uri(BASE_PATH, teamInstance.id())
                .contentType(APPLICATION_JSON)
                .bodyValue(match)
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(MatchResponse.class)
                .value(matchResponse -> Assertions.assertEquals(playersInstance.size(), matchResponse.players().size()))
                .value(matchResponse -> Assertions.assertEquals(match.opponent(), matchResponse.opponent()))
                .value(matchResponse -> Assertions.assertTrue(matchResponse.details().contains(match.detail())))
                .value(matchResponse -> Assertions.assertEquals(MatchSelector.FUTURE, matchResponse.matchSelector()))
                .value(matchResponse -> Assertions.assertEquals(match.location(), matchResponse.location()))
                .value(matchResponse -> Assertions.assertNotNull(matchResponse.id()))
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Attempt to create a match with a unknown player")
    void attemptToCreateMatchWithUnknownPlayer()  {

        client.post()
                .uri(BASE_PATH, teamInstance.id())
                .contentType(APPLICATION_JSON)
                .bodyValue(createMatchRequest(Set.of(faker.idNumber().valid())))
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorInfo.class)
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Successful create 10 Matches and return it paged")
    void successfulCreateTenMatches()  {
        createMatchs(10);

        Assertions.assertEquals(10, repository.findAll().size());

        client.get()
                .uri(BASE_PATH, teamInstance.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(PageableResponse.class)
                .value(pageableResponse -> assertEquals(10, pageableResponse.getSize()))
                .value(pageableResponse -> assertEquals(1, pageableResponse.getTotalPages()))
                .value(pageableResponse -> assertEquals(10, pageableResponse.getTotalElements()))
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Get past Matches and return it paged")
    void successfulGetPastMatches()  {
        final List<MatchesEntity> pastMatchs = createPastMatchs(10);

        Assertions.assertEquals(pastMatchs.size(), repository.count());


        client.get()
                .uri(BASE_PATH, teamInstance.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(getTypeReference())
                .value(pageableResponse -> assertEquals(10, pageableResponse.getSize()))
                .value(pageableResponse -> assertEquals(1, pageableResponse.getTotalPages()))
                .value(pageableResponse -> assertEquals(10, pageableResponse.getTotalElements()))
                .value(pageableResponse -> {
                    final List<MatchResponse> content = pageableResponse.getContent();
                    final MatchResponse matchResponse = content.get(0);
                    Assertions.assertEquals(MatchSelector.PAST, matchResponse.matchSelector());
                })
                .consumeWith(consume -> log.debug("{}", consume) );
    }

    @Test
    @DisplayName("Get past and future Matches and return it paged")
    void successfulGetPastFutureMatches()  {
        final List<MatchesEntity> pastMatchs = createPastMatchs(10);
        final List<MatchesEntity> futureMatchs = createFutureMatchs(10);

        Assertions.assertEquals(pastMatchs.size() + futureMatchs.size(), repository.count());


        client.get()
                .uri(BASE_PATH, teamInstance.id())
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(getTypeReference())
                .value(pageableResponse -> assertEquals(20, pageableResponse.getSize()))
                .value(pageableResponse -> assertEquals(1, pageableResponse.getTotalPages()))
                .value(pageableResponse -> assertEquals(20, pageableResponse.getTotalElements()))
                .value(pageableResponse -> {
                    assertEquals(10, pageableResponse.getContent().stream().filter(i-> MatchSelector.PAST.equals(i.matchSelector())).count());
                    assertEquals(10, pageableResponse.getContent().stream().filter(i-> MatchSelector.FUTURE.equals(i.matchSelector())).count());
                })
                .consumeWith(consume -> log.debug("{}", consume) );
    }


    @Test
    @DisplayName("Successful patch a match")
    void successfulPatchMatch()  {

        final List<MatchesEntity> futureMatchs = createFutureMatchs(1);
        final MatchesEntity match = futureMatchs.get(0);

        final PatchMatchRequest patchMatchRequest = new PatchMatchRequest(
                faker.rickAndMorty().character(),
                faker.starTrek().location(),
                null,
                null,
                null
        );

        client.patch()
                .uri(BASE_PATH + "/{matchId}", teamInstance.id(), match.getId())
                .contentType(APPLICATION_JSON)
                .bodyValue(patchMatchRequest)
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(consume -> log.debug("{}", consume) );

        final MatchesEntity updatedMatch = repository.findById(match.getId()).get();

        Assertions.assertEquals(patchMatchRequest.opponent(), updatedMatch.getOpponent());
        Assertions.assertEquals(patchMatchRequest.location(), updatedMatch.getLocation());
        Assertions.assertEquals(MatchSelector.FUTURE, MatchSelector.get(updatedMatch.getDate()));
        Assertions.assertNotNull(updatedMatch.getPlayers());
        Assertions.assertNotNull(updatedMatch.getDate());
        Assertions.assertNotNull(updatedMatch.getDetails());
    }

    @Test
    @DisplayName("Successful add note to a match")
    void successfulAddNoteMatch()  {

        final List<MatchesEntity> futureMatchs = createFutureMatchs(1);
        final MatchesEntity match = futureMatchs.get(0);

        final PutDetailMatchRequest detailMatchRequest = new PutDetailMatchRequest(faker.shakespeare().asYouLikeItQuote());


        client.put()
                .uri(BASE_PATH + "/{matchId}/details", teamInstance.id(), match.getId())
                .contentType(APPLICATION_JSON)
                .bodyValue(detailMatchRequest)
                .accept(APPLICATION_JSON)
                .header("Authorization", getAccessToken())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .consumeWith(consume -> log.debug("{}", consume) );

        final MatchesEntity updatedMatch = repository.findById(match.getId()).get();

        Assertions.assertTrue(updatedMatch.getDetails().contains(detailMatchRequest.detail()));
        Assertions.assertEquals(2, updatedMatch.getDetails().size());

        Assertions.assertNotNull(updatedMatch.getPlayers());
        Assertions.assertNotNull(updatedMatch.getDate());
        Assertions.assertNotNull(updatedMatch.getDetails());

    }

    private ParameterizedTypeReference<PageableResponse<MatchResponse>> getTypeReference() {
        return new ParameterizedTypeReference<>() {};
    }

    private List<MatchesEntity> createPastMatchs(int amount){
        List<MatchesEntity> matchRequests = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            matchRequests.add(createMatchEntity(LocalDateTime.now().minusMonths(4L)));
        }
        return repository.saveAll(matchRequests);
    }

    private List<MatchesEntity> createFutureMatchs(int amount){
        List<MatchesEntity> matchRequests = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            matchRequests.add(createMatchEntity(LocalDateTime.now().plusMonths(4L)));
        }
        return repository.saveAll(matchRequests);
    }

    private void createMatchs(int amount){
        for (int i = 0; i < amount; i++) {
                client.post()
                    .uri(BASE_PATH, teamInstance.id())
                    .contentType(APPLICATION_JSON)
                    .bodyValue(createMatchRequest())
                    .accept(APPLICATION_JSON)
                    .header("Authorization", getAccessToken())
                    .exchange();
        }
    }


    private CreateMatchRequest createMatchRequest(LocalDateTime  matchDate, Set<String>  player){
        return new CreateMatchRequest(
                faker.rickAndMorty().character(),
                faker.rickAndMorty().location(),
                matchDate,
                faker.hitchhikersGuideToTheGalaxy().quote(),
                player
        );
    }

    private CreateMatchRequest createMatchRequest(){
        return createMatchRequest(
                getPlayer()
        );
    }
    private CreateMatchRequest createMatchRequest(Set<String>  player){
        return createMatchRequest(LocalDateTime.now().plusMonths(4L), player);
    }

    private CreateMatchRequest createMatchRequest(LocalDateTime  matchDate){
        return createMatchRequest(matchDate, getPlayer());
    }

    private MatchesEntity createMatchEntity(LocalDateTime  matchDate){
        final CreateMatchRequest matchRequest = createMatchRequest(matchDate);
        final Set<PlayerResponse> players = Set.copyOf(createPlayers(10, teamInstance));

        return mapper.from(matchRequest, players, teamInstance);
    }

    private Set<String> getPlayer() {
        return playersInstance.stream().map(PlayerResponse::id).collect(Collectors.toSet());
    }
}
