package com.thinkific.sportsapi.api.controller;

import com.thinkific.sportsapi.api.domain.Pageable;
import com.thinkific.sportsapi.api.domain.PageableResponse;
import com.thinkific.sportsapi.api.domain.teams.CreateTeamRequest;
import com.thinkific.sportsapi.api.domain.teams.TeamResponse;
import com.thinkific.sportsapi.usecase.CreateTeamCase;
import com.thinkific.sportsapi.usecase.GetPageableTeamCase;
import com.thinkific.sportsapi.usecase.GetTeamCase;
import com.thinkific.sportsapi.usecase.UpdateTeamCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@RestController
@RequestMapping("/v1/teams")
public final class TeamsController extends BaseController {
    private final Logger log = LoggerFactory.getLogger(TeamsController.class);

    private final CreateTeamCase createTeamCase;
    private final GetPageableTeamCase pageableTeam;
    private final GetTeamCase getTeamCase;
    private final UpdateTeamCase updateTeamCase;

    public TeamsController(CreateTeamCase createTeamCase, GetPageableTeamCase pageableTeam, GetTeamCase getTeamCase, UpdateTeamCase updateTeamCase) {
        this.createTeamCase = createTeamCase;
        this.pageableTeam = pageableTeam;
        this.getTeamCase = getTeamCase;
        this.updateTeamCase = updateTeamCase;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamRequest request,
                                                   @AuthenticationPrincipal Jwt principal,
                                                   UriComponentsBuilder builder) {
        log.trace("CreateTeamRequest {}", request);

        final String userEmail = getUserEmail(principal);
        final TeamResponse response = this.createTeamCase.handle(request, userEmail);
        UriComponents uriComponents = builder.path("/v1/teams/{id}").buildAndExpand(response.id());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(uriComponents.toUri())
                .body(response);

    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    PageableResponse<TeamResponse> getTeams(@Valid Pageable pageable,
                                            @AuthenticationPrincipal Jwt principal) {
        log.trace("Pageable {}", pageable);
        final String userEmail = getUserEmail(principal);

        return pageableTeam.handle(userEmail, pageable);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    TeamResponse getTeamById(@PathVariable String id,
                             @AuthenticationPrincipal Jwt principal) {
        log.trace("getTeamById {}", id);

        final String userEmail = getUserEmail(principal);

        return getTeamCase.handle(userEmail, id);
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTeam(@PathVariable String id,
                           @NotNull @RequestBody CreateTeamRequest patchRequest,
                           @AuthenticationPrincipal Jwt principal) {

        log.trace("update id {} patch {}", id, patchRequest);

        final String userEmail = getUserEmail(principal);

        this.updateTeamCase.handle(userEmail, id, patchRequest);

    }


}
