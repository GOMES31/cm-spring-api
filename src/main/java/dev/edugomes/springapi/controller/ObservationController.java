package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.request.CreateObservationRequest;
import dev.edugomes.springapi.dto.request.UpdateObservationRequest;
import dev.edugomes.springapi.dto.response.ObservationResponse;
import dev.edugomes.springapi.exception.ObservationNotFoundException;
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
import dev.edugomes.springapi.util.GlobalMethods;

@RestController
@RequestMapping("/observations")
@RequiredArgsConstructor
public class ObservationController {

    private final ObservationService observationService;

    private static final String CREATE_OBSERVATION = "/create";
    private static final String UPDATE_OBSERVATION = "/update";
    private static final String GET_OBSERVATION_BY_ID = "/{id}";

    @PostMapping(
            value = CREATE_OBSERVATION,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ObservationResponse>> createObservation(@Valid @RequestBody CreateObservationRequest createObservationRequest) {
        try {
            String userEmail = GlobalMethods.getCurrentUserEmail();
            ObservationResponse observationResponse = observationService.createObservation(createObservationRequest, userEmail);
            return ResponseHandler.buildResponse("Observation created successfully", HttpStatus.CREATED, observationResponse);
        } catch (TaskNotFoundException e) {
            return ResponseHandler.buildResponse("Task not found: " + e.getMessage(), HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to create observation: " + e.getMessage(), HttpStatus.FORBIDDEN, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error creating observation: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @PutMapping(
            value = UPDATE_OBSERVATION,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ObservationResponse>> updateObservation(@Valid @RequestBody UpdateObservationRequest updateObservationRequest) {
        try {
            if (updateObservationRequest.getId() == null) {
                return ResponseHandler.buildResponse("Observation ID is required for update", HttpStatus.BAD_REQUEST, null);
            }
            String userEmail = GlobalMethods.getCurrentUserEmail();
            ObservationResponse observationResponse = observationService.updateObservation(updateObservationRequest.getId(), updateObservationRequest, userEmail);
            return ResponseHandler.buildResponse("Observation updated successfully", HttpStatus.OK, observationResponse);
        } catch (ObservationNotFoundException e) {
            return ResponseHandler.buildResponse("Observation not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to update this observation", HttpStatus.FORBIDDEN, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error updating observation: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping(
            value = GET_OBSERVATION_BY_ID,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ObservationResponse>> getObservationById(@PathVariable Long id) {
        try {
            String userEmail = GlobalMethods.getCurrentUserEmail();
            ObservationResponse observationResponse = observationService.getObservationById(id, userEmail);
            return ResponseHandler.buildResponse("Observation retrieved successfully", HttpStatus.OK, observationResponse);
        } catch (ObservationNotFoundException e) {
            return ResponseHandler.buildResponse("Observation not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to access this observation", HttpStatus.FORBIDDEN, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error retrieving observation: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}