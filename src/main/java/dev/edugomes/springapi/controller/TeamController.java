package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.request.CreateTeamRequest;
import dev.edugomes.springapi.dto.request.AddTeamMemberRequest;
import dev.edugomes.springapi.dto.request.UpdateTeamMemberRequest;
import dev.edugomes.springapi.dto.request.UpdateTeamRequest;
import dev.edugomes.springapi.dto.response.TeamMemberResponse;
import dev.edugomes.springapi.dto.response.TeamResponse;
import dev.edugomes.springapi.exception.TeamNotFoundException;
import dev.edugomes.springapi.exception.UserNotFoundException;
import dev.edugomes.springapi.service.team.TeamService;
import dev.edugomes.springapi.utils.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private static final String UPDATE_MEMBER = "/{teamId}/update-member/{memberId}";


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

    @PostMapping(
            value = ADD_MEMBER,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<TeamMemberResponse>> addMember(
            @PathVariable("teamId") Long teamId,
            @RequestBody AddTeamMemberRequest request
    ) {
        try {
            TeamMemberResponse response = teamService.addMember(teamId, request);
            return ResponseHandler.buildResponse("Member added successfully", HttpStatus.OK, response);
        } catch (UserNotFoundException e) {
            return ResponseHandler.buildResponse("User not found: " + e.getMessage(), HttpStatus.NOT_FOUND, null);
        } catch (TeamNotFoundException e) {
            return ResponseHandler.buildResponse("Team not found: " + e.getMessage(), HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not add member to team: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
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

    @PutMapping(
            value = UPDATE_MEMBER,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<TeamMemberResponse>> updateMember(
            @PathVariable("teamId") Long teamId,
            @PathVariable("memberId") Long memberId,
            @RequestBody UpdateTeamMemberRequest request
    ) {
        try {
            TeamMemberResponse response = teamService.updateMember(teamId, memberId, request);
            return ResponseHandler.buildResponse("Member updated successfully", HttpStatus.OK, response);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not update member", HttpStatus.BAD_REQUEST, null);
        }
    }
}
