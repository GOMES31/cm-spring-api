package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.request.CreateTeamRequest;
import dev.edugomes.springapi.dto.request.AddTeamMemberRequest;
import dev.edugomes.springapi.dto.request.UpdateTeamRequest;
import dev.edugomes.springapi.dto.response.TeamResponse;
import dev.edugomes.springapi.service.team.TeamService;
import dev.edugomes.springapi.util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    private static final String CREATE_TEAM = "/create";
    private static final String GET_TEAM_BY_ID = "/{teamId}";
    private static final String ADD_MEMBER = "/{teamId}/add-member";
    private static final String UPDATE_PROFILE = "/{teamId}/update-profile";
    private static final String REMOVE_MEMBER = "/{teamId}/remove-member/{memberId}";


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
            return ResponseHandler.buildResponse("Could not create team", HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping(
        value = GET_TEAM_BY_ID,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<TeamResponse>> getTeamById(@PathVariable("teamId") Long teamId) {
        try {
            TeamResponse response = teamService.getTeamById(teamId);
            return ResponseHandler.buildResponse("Team retrieved successfully", HttpStatus.OK, response);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not retrieve team", HttpStatus.NOT_FOUND, null);
        }
    }
    @PostMapping(
            value = ADD_MEMBER,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> addMember(
            @PathVariable("teamId") Long teamId,
            @RequestBody AddTeamMemberRequest request
    ) {
        try {
            teamService.addMember(teamId, request);
            return ResponseHandler.buildResponse("Member added successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not add member to team", HttpStatus.BAD_REQUEST, null);
        }
    }

    @PutMapping(
            value = UPDATE_PROFILE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<TeamResponse>> updateTeam(
            @PathVariable("teamId") Long teamId,
            @RequestBody UpdateTeamRequest request
    ) {
        try {
            TeamResponse response = teamService.updateTeam(teamId, request);
            return ResponseHandler.buildResponse("Team profile updated successfully", HttpStatus.OK, response);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not update team profile", HttpStatus.BAD_REQUEST, null);
        }
    }

    @DeleteMapping(
            value = REMOVE_MEMBER,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable("teamId") Long teamId,
            @PathVariable("memberId") Long memberId
    ) {
        try {
            teamService.removeMember(teamId, memberId);
            return ResponseHandler.buildResponse("Member removed successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not remove member from team", HttpStatus.BAD_REQUEST, null);
        }
    }
}
