package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.request.CreateTaskRequest;
import dev.edugomes.springapi.dto.request.UpdateTaskRequest;
import dev.edugomes.springapi.dto.response.TaskResponse;
import dev.edugomes.springapi.dto.response.TaskResponse.ObservationInfo;
import dev.edugomes.springapi.exception.ProjectNotFoundException;
import dev.edugomes.springapi.exception.TaskNotFoundException;
import dev.edugomes.springapi.exception.UnauthorizedException;
import dev.edugomes.springapi.service.task.TaskService;
import dev.edugomes.springapi.utils.GlobalMethods;
import dev.edugomes.springapi.utils.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private static final String CREATE_TASK_ENDPOINT = "/create";
    private static final String UPDATE_TASK_ENDPOINT = "/update";
    private static final String GET_TASK_BY_ID_ENDPOINT = "/{id}";
    private static final String GET_TASK_OBSERVATIONS_ENDPOINT = "/{id}/observations";

    @PostMapping(
            value = CREATE_TASK_ENDPOINT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody CreateTaskRequest createTaskRequest) {
        try {
            String userEmail = GlobalMethods.getCurrentUserEmail();
            TaskResponse taskResponse =
                    taskService.createTask(createTaskRequest, userEmail);
            return ResponseHandler.buildResponse("Task created successfully", HttpStatus.CREATED, taskResponse);
        } catch (ProjectNotFoundException e) {
            return ResponseHandler.buildResponse("Project not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to create task", HttpStatus.FORBIDDEN, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error creating task: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @PutMapping(
            value = UPDATE_TASK_ENDPOINT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @Valid @RequestBody UpdateTaskRequest updateTaskRequest) {
        try {
            if (updateTaskRequest.getId() == null) {
                return ResponseHandler.buildResponse("Task ID is required for update", HttpStatus.BAD_REQUEST, null);
            }
            String userEmail = GlobalMethods.getCurrentUserEmail();
            TaskResponse taskResponse = taskService.updateTask(updateTaskRequest.getId(), updateTaskRequest, userEmail);
            return ResponseHandler.buildResponse("Task updated successfully", HttpStatus.OK, taskResponse);
        } catch (TaskNotFoundException e) {
            return ResponseHandler.buildResponse("Task not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to update task", HttpStatus.FORBIDDEN, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error updating task: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping(
            value = GET_TASK_BY_ID_ENDPOINT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @PathVariable Long id) {
        try {
            String userEmail = GlobalMethods.getCurrentUserEmail();
            TaskResponse taskResponse = taskService.getTaskById(id, userEmail);
            return ResponseHandler.buildResponse("Task retrieved successfully", HttpStatus.OK, taskResponse);
        } catch (TaskNotFoundException e) {
            return ResponseHandler.buildResponse("Task not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to access task", HttpStatus.FORBIDDEN, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error retrieving task: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping(
            value = GET_TASK_OBSERVATIONS_ENDPOINT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<ObservationInfo>>> getObservationsForTask(
            @PathVariable Long id) {
        try {
            String userEmail = GlobalMethods.getCurrentUserEmail();
            List<ObservationInfo> observations = taskService.getObservationsForTask(id, userEmail);
            return ResponseHandler.buildResponse("Observations retrieved successfully", HttpStatus.OK, observations);
        } catch (TaskNotFoundException e) {
            return ResponseHandler.buildResponse("Task not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to access observations for this task", HttpStatus.FORBIDDEN, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error retrieving observations for task: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}