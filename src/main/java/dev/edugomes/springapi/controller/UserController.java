package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.request.UpdateUserProfileRequest;
import dev.edugomes.springapi.dto.response.*;
import dev.edugomes.springapi.exception.UserNotFoundException;
import dev.edugomes.springapi.service.user.UserService;
import dev.edugomes.springapi.utils.ResponseHandler;
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
    private static final String GET_TASKS = "/tasks";
    private static final String GET_PROJECTS = "/projects";
    private static final String GET_OBSERVATIONS = "/observations";


    @PutMapping(
            value = UPDATE_PROFILE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        try {
            UserProfileResponse response = userService.updateProfile(request);
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
            List<TeamResponse> teams = userService.getTeams();

            if(teams.isEmpty()) {
                return ResponseHandler.buildResponse("No teams found for the user", HttpStatus.NOT_FOUND, null);
            }
            return ResponseHandler.buildResponse("Teams retrieved successfully", HttpStatus.OK, teams);
        } catch (UserNotFoundException e) {
            return ResponseHandler.buildResponse("User not found", HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not retrieve teams", HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping(
            value = GET_TASKS,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasks(){
        try {
            List<TaskResponse> tasks = userService.getTasksForAuthenticatedUser();
            if(tasks.isEmpty()) {
                return ResponseHandler.buildResponse("No tasks found for the user", HttpStatus.OK, null);
            }
            return ResponseHandler.buildResponse("Tasks retrieved successfully", HttpStatus.OK, tasks);
        } catch (UserNotFoundException e) {
            return ResponseHandler.buildResponse("User not found", HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not retrieve tasks", HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping(
            value = GET_PROJECTS,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects(){
        try {
            List<ProjectResponse> projects = userService.getProjectsForAuthenticatedUser();
            if(projects.isEmpty()) {
                return ResponseHandler.buildResponse("No projects found for the user", HttpStatus.OK, null);
            }
            return ResponseHandler.buildResponse("Projects retrieved successfully", HttpStatus.OK, projects);
        } catch (UserNotFoundException e) {
            return ResponseHandler.buildResponse("User not found", HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not retrieve projects", HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping(
            value = GET_OBSERVATIONS,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<ObservationResponse>>> getObservations(){
        try {
            List<ObservationResponse> observations = userService.getObservationsForAuthenticatedUser();
            if(observations.isEmpty()) {
                return ResponseHandler.buildResponse("No observations found for the user", HttpStatus.OK, null);
            }
            return ResponseHandler.buildResponse("Observations retrieved successfully", HttpStatus.OK, observations);
        } catch (UserNotFoundException e) {
            return ResponseHandler.buildResponse("User not found", HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not retrieve observations", HttpStatus.BAD_REQUEST, null);
        }
    }
}