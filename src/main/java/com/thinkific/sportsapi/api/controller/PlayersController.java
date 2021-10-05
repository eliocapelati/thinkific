package com.thinkific.sportsapi.api.controller;

import com.thinkific.sportsapi.api.domain.Pageable;
import com.thinkific.sportsapi.api.domain.PageableResponse;
import com.thinkific.sportsapi.api.domain.players.CreatePlayerRequest;
import com.thinkific.sportsapi.api.domain.players.PlayerResponse;
import com.thinkific.sportsapi.usecase.CreatePlayerCase;
import com.thinkific.sportsapi.usecase.DeletePlayerCase;
import com.thinkific.sportsapi.usecase.GetPageablePlayerCase;
import com.thinkific.sportsapi.usecase.UpdatePlayerCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/teams/{teamId}/players")
public final class PlayersController extends BaseController {
    private final Logger log = LoggerFactory.getLogger(PlayersController.class);

    private final CreatePlayerCase createPlayerCase;
    private final GetPageablePlayerCase pageablePlayers;
    private final DeletePlayerCase deletePlayerCase;
    private final UpdatePlayerCase updatePlayerCase;

    public PlayersController(CreatePlayerCase createPlayerCase, GetPageablePlayerCase pageablePlayers, DeletePlayerCase deletePlayerCase, UpdatePlayerCase updatePlayerCase) {
        this.createPlayerCase = createPlayerCase;
        this.pageablePlayers = pageablePlayers;
        this.deletePlayerCase = deletePlayerCase;
        this.updatePlayerCase = updatePlayerCase;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PlayerResponse createPlayer(@AuthenticationPrincipal Jwt principal,
                                       @PathVariable String teamId,
                                       @Valid @RequestBody CreatePlayerRequest request) {
        log.trace("CreatePlayerRequest {}", request);
        final String email = getUserEmail(principal);
        return this.createPlayerCase.handle(teamId, email, request);
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody PageableResponse<PlayerResponse> getPlayers(@AuthenticationPrincipal Jwt principal,
                                                                     @PathVariable String teamId,
                                                                     @Valid Pageable pageable) {

        log.trace("getPlayers {} teamId {}", pageable, teamId);
        final String userEmail = getUserEmail(principal);

        return pageablePlayers.handle(userEmail, teamId, pageable);

    }

    @DeleteMapping(path = "/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlayer(@AuthenticationPrincipal Jwt principal,
                             @PathVariable String teamId,
                             @PathVariable String playerId){

        log.trace("deletePlayer PlayerId {} teamId {}", playerId, teamId);
        final String userEmail = getUserEmail(principal);

        this.deletePlayerCase.handle(userEmail, playerId, teamId);
    }

    @PatchMapping(path = "/{playerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePlayer(@AuthenticationPrincipal Jwt principal,
                             @PathVariable String teamId,
                             @PathVariable String playerId,
                             @RequestBody CreatePlayerRequest request){

        log.trace("updatePlayer PlayerId {} teamId {} request {}", playerId, teamId, request);
        final String userEmail = getUserEmail(principal);

        this.updatePlayerCase.handle(userEmail, playerId, teamId, request);
    }



}
