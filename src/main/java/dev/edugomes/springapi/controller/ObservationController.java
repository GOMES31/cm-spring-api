package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.request.CreateObservationRequest;
import dev.edugomes.springapi.dto.request.UpdateObservationRequest;
import dev.edugomes.springapi.dto.response.ObservationResponse;
import dev.edugomes.springapi.exception.ObservationNotFoundException;
import dev.edugomes.springapi.exception.ProjectNotFoundException;
import dev.edugomes.springapi.exception.TaskNotFoundException;
import dev.edugomes.springapi.exception.UnauthorizedException;
import dev.edugomes.springapi.service.observation.ObservationService;
import dev.edugomes.springapi.util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/observations")
@RequiredArgsConstructor
public class ObservationController {

    private final ObservationService observationService;

    private static final String CREATE_OBSERVATION = "";
    private static final String GET_OBSERVATION_BY_ID = "/{id}";
    private static final String GET_ALL_OBSERVATIONS = "";
    private static final String GET_OBSERVATIONS_BY_TASK_ID = "/task/{taskId}";
    private static final String GET_OBSERVATIONS_BY_USER_ID = "/user/{userId}";
    private static final String UPDATE_OBSERVATION = "/{id}";
    private static final String DELETE_OBSERVATION = "/{id}";

    @PostMapping(
            value = CREATE_OBSERVATION,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ObservationResponse>> createObservation(@Valid @RequestBody CreateObservationRequest createObservationRequest) {
        try {
            ObservationResponse observationResponse = observationService.createObservation(createObservationRequest);
            return ResponseHandler.buildResponse("Observation created successfully", HttpStatus.CREATED, observationResponse);
        } catch (TaskNotFoundException e) {
            return ResponseHandler.buildResponse("Task not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to create observation for this task", HttpStatus.FORBIDDEN, null);
        }
    }

    @GetMapping(
            value = GET_OBSERVATION_BY_ID,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ObservationResponse>> getObservationById(@PathVariable Long id) {
        try {
            ObservationResponse observationResponse = observationService.getObservationById(id);
            return ResponseHandler.buildResponse("Observation retrieved successfully", HttpStatus.OK, observationResponse);
        } catch (ObservationNotFoundException e) {
            return ResponseHandler.buildResponse("Observation not found", HttpStatus.NOT_FOUND, null);
        }
    }

    @GetMapping(
            value = GET_ALL_OBSERVATIONS,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<ObservationResponse>>> getAllObservations() {
        List<ObservationResponse> observations = observationService.getAllObservations();
        return ResponseHandler.buildResponse("All observations retrieved successfully", HttpStatus.OK, observations);
    }

    @GetMapping(
            value = GET_OBSERVATIONS_BY_TASK_ID,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<ObservationResponse>>> getObservationsByTaskId(@PathVariable Long taskId) {
        try {
            List<ObservationResponse> observations = observationService.getObservationsByTaskId(taskId);
            return ResponseHandler.buildResponse("Observations for task retrieved successfully", HttpStatus.OK, observations);
        } catch (TaskNotFoundException e) {
            return ResponseHandler.buildResponse("Task not found", HttpStatus.NOT_FOUND, null);
        }
    }

    @GetMapping(
            value = GET_OBSERVATIONS_BY_USER_ID,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<ObservationResponse>>> getObservationsByUserId(@PathVariable Long userId) {
        List<ObservationResponse> observations = observationService.getObservationsByUserId(userId);
        return ResponseHandler.buildResponse("Observations by user retrieved successfully", HttpStatus.OK, observations);
    }

    @PutMapping(
            value = UPDATE_OBSERVATION,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ObservationResponse>> updateObservation(@PathVariable Long id, @Valid @RequestBody UpdateObservationRequest updateObservationRequest) {
        try {
            ObservationResponse observationResponse = observationService.updateObservation(id, updateObservationRequest);
            return ResponseHandler.buildResponse("Observation updated successfully", HttpStatus.OK, observationResponse);
        } catch (ObservationNotFoundException e) {
            return ResponseHandler.buildResponse("Observation not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to update this observation", HttpStatus.FORBIDDEN, null);
        }
    }

    @DeleteMapping(
            value = DELETE_OBSERVATION,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> deleteObservation(@PathVariable Long id) {
        try {
            observationService.deleteObservation(id);
            return ResponseHandler.buildResponse("Observation deleted successfully", HttpStatus.NO_CONTENT, null);
        } catch (ObservationNotFoundException e) {
            return ResponseHandler.buildResponse("Observation not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to delete this observation", HttpStatus.FORBIDDEN, null);
        }
    }
}