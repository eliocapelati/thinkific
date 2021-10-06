package com.thinkific.sportsapi.api.controller;

import com.thinkific.sportsapi.api.domain.Pageable;
import com.thinkific.sportsapi.api.domain.PageableResponse;
import com.thinkific.sportsapi.api.domain.matches.*;
import com.thinkific.sportsapi.usecase.CreateMatchCase;
import com.thinkific.sportsapi.usecase.GetPageableMatchCase;
import com.thinkific.sportsapi.usecase.PutMatchDetailsCase;
import com.thinkific.sportsapi.usecase.UpdateMatchCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/teams/{teamId}/matches")
public final class MatchesController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(MatchesController.class);

    private final CreateMatchCase createMatchCase;
    private final GetPageableMatchCase getPageableMatchCase;
    private final UpdateMatchCase updateMatchCase;
    private final PutMatchDetailsCase putMatchDetailsCase;


    public MatchesController(CreateMatchCase createMatchCase,
                             GetPageableMatchCase getPageableMatchCase,
                             UpdateMatchCase updateMatchCase,
                             PutMatchDetailsCase putMatchDetailsCase) {

        this.createMatchCase = createMatchCase;
        this.getPageableMatchCase = getPageableMatchCase;
        this.updateMatchCase = updateMatchCase;
        this.putMatchDetailsCase = putMatchDetailsCase;
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces =  MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MatchResponse createMatch(@AuthenticationPrincipal Jwt principal,
                                     @PathVariable String teamId,
                                     @Valid @RequestBody CreateMatchRequest request){

        log.trace("CreateMatchRequest for team {} using {}", teamId, request);
        final String email = getUserEmail(principal);

        return createMatchCase.handle(email, teamId, request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PageableResponse<MatchResponse> getMatch(@AuthenticationPrincipal Jwt principal,
                                                    @PathVariable String teamId,
                                                    @Valid Pageable page,
                                                    @RequestParam(required = false) @Valid MatchSelector match){

        log.trace("getMatch {} page {} selector {}", teamId, page, match);
        final String email = getUserEmail(principal);

        return getPageableMatchCase.handle(email, teamId, match, page);
    }

    @PatchMapping(path = "/{matchId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMatch(@AuthenticationPrincipal Jwt principal,
                            @PathVariable String teamId,
                            @PathVariable String matchId,
                            @RequestBody PatchMatchRequest patch){

        log.trace("updateMatch  team {} match {} patch {}", teamId, matchId, patch);
        final String email = getUserEmail(principal);

        updateMatchCase.handle(patch, teamId, email, matchId);
    }

    @PutMapping(path = "/{matchId}/details", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createMatchDetails(@AuthenticationPrincipal Jwt principal,
                                   @PathVariable String teamId,
                                   @PathVariable String matchId,
                                   @RequestBody @Valid PutDetailMatchRequest putRequest){
        log.trace("createMatchDetails  team {} match {} putRequest {}", teamId, matchId, putRequest);

        final String email = getUserEmail(principal);

        putMatchDetailsCase.handle(putRequest, email, teamId, matchId);
    }
}
