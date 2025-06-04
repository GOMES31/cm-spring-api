package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.domain.Team;
import dev.edugomes.springapi.dto.request.UpdateUserProfileRequest;
import dev.edugomes.springapi.dto.response.TeamResponse;
import dev.edugomes.springapi.dto.response.UpdateUserProfileResponse;
import dev.edugomes.springapi.exception.UserNotFoundException;
import dev.edugomes.springapi.mapper.Mapper;
import dev.edugomes.springapi.service.user.UserService;
import dev.edugomes.springapi.util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private static final String UPDATE_PROFILE = "/update-profile";
    private static final String GET_TEAMS = "/teams";

    @PatchMapping(
            value = UPDATE_PROFILE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<UpdateUserProfileResponse>> updateUserProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        try {
            UpdateUserProfileResponse response = userService.updateProfile(request);
            return ResponseHandler.buildResponse("Profile updated successfully", HttpStatus.OK, response);
        } catch (UserNotFoundException e) {
            return ResponseHandler.buildResponse("User not found", HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not update profile", HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping(
            value = GET_TEAMS,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<TeamResponse>>> getTeams(){
        try {
            List<Team> teams = userService.getTeams();

            if(teams.isEmpty()) {
                return ResponseHandler.buildResponse("No teams found for the user", HttpStatus.NOT_FOUND, null);
            }

            List<TeamResponse> teamResponses = teams.stream().map(Mapper::toTeamResponse).toList();
            return ResponseHandler.buildResponse("Teams retrieved successfully", HttpStatus.OK, teamResponses);
        } catch (UserNotFoundException e) {
            return ResponseHandler.buildResponse("User not found", HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not retrieve teams", HttpStatus.BAD_REQUEST, null);
        }
    }

}