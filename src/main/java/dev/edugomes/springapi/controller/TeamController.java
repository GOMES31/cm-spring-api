package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.request.CreateTeamRequest;
import dev.edugomes.springapi.dto.response.TeamResponse;
import dev.edugomes.springapi.service.team.TeamService;
import dev.edugomes.springapi.util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

    private static final String CREATE_TEAM = "/create";

    @PostMapping(
            value = CREATE_TEAM,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<TeamResponse>> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        try {
            TeamResponse response = teamService.create(request);
            return ResponseHandler.buildResponse("Team created successfully", HttpStatus.CREATED, response);
        } catch (Exception e) {
            logger.error("Exception", e);
            return ResponseHandler.buildResponse("Could not create team", HttpStatus.BAD_REQUEST, null);
        }
    }
}
