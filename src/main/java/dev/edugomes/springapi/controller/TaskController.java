package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.request.CreateTaskRequest;
import dev.edugomes.springapi.dto.request.UpdateTaskRequest;
import dev.edugomes.springapi.dto.response.TaskResponse;
import dev.edugomes.springapi.exception.TaskNotFoundException;
import dev.edugomes.springapi.exception.ProjectNotFoundException;
import dev.edugomes.springapi.exception.UnauthorizedException;
import dev.edugomes.springapi.service.task.TaskService;
import dev.edugomes.springapi.util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private static final String CREATE_TASK = "";
    private static final String GET_ALL_TASKS = "";
    private static final String GET_TASK_BY_ID = "/{id}";
    private static final String UPDATE_TASK = "/{id}";
    private static final String DELETE_TASK = "/{id}";
    private static final String GET_TASKS_BY_PROJECT = "/project/{projectId}";
    private static final String GET_TASKS_BY_USER = "/user";

    @PostMapping(
            value = CREATE_TASK,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody CreateTaskRequest createTaskRequest,
            Authentication authentication) {
        try {
            TaskResponse taskResponse = taskService.createTask(createTaskRequest, authentication.getName());
            return ResponseHandler.buildResponse("Task created successfully", HttpStatus.CREATED, taskResponse);
        } catch (ProjectNotFoundException e) {
            return ResponseHandler.buildResponse("Project not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to create task", HttpStatus.FORBIDDEN, null);
        }
    }

    @GetMapping(
            value = GET_ALL_TASKS,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTasks(Authentication authentication) {
        try {
            List<TaskResponse> tasks = taskService.getAllTasksForUser(authentication.getName());
            return ResponseHandler.buildResponse("Tasks retrieved successfully", HttpStatus.OK, tasks);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error retrieving tasks", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping(
            value = GET_TASK_BY_ID,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            TaskResponse taskResponse = taskService.getTaskById(id, authentication.getName());
            return ResponseHandler.buildResponse("Task retrieved successfully", HttpStatus.OK, taskResponse);
        } catch (TaskNotFoundException e) {
            return ResponseHandler.buildResponse("Task not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to access task", HttpStatus.FORBIDDEN, null);
        }
    }

    @PutMapping(
            value = UPDATE_TASK,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest updateTaskRequest,
            Authentication authentication) {
        try {
            TaskResponse taskResponse = taskService.updateTask(id, updateTaskRequest, authentication.getName());
            return ResponseHandler.buildResponse("Task updated successfully", HttpStatus.OK, taskResponse);
        } catch (TaskNotFoundException e) {
            return ResponseHandler.buildResponse("Task not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to update task", HttpStatus.FORBIDDEN, null);
        }
    }

    @DeleteMapping(
            value = DELETE_TASK,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            taskService.deleteTask(id, authentication.getName());
            return ResponseHandler.buildResponse("Task deleted successfully", HttpStatus.OK, null);
        } catch (TaskNotFoundException e) {
            return ResponseHandler.buildResponse("Task not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to delete task", HttpStatus.FORBIDDEN, null);
        }
    }

    @GetMapping(
            value = GET_TASKS_BY_PROJECT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByProject(
            @PathVariable Long projectId,
            Authentication authentication) {
        try {
            List<TaskResponse> tasks = taskService.getTasksByProject(projectId, authentication.getName());
            return ResponseHandler.buildResponse("Tasks retrieved successfully", HttpStatus.OK, tasks);
        } catch (ProjectNotFoundException e) {
            return ResponseHandler.buildResponse("Project not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to access project tasks", HttpStatus.FORBIDDEN, null);
        }
    }

    @GetMapping(
            value = GET_TASKS_BY_USER,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksByUser(Authentication authentication) {
        try {
            List<TaskResponse> tasks = taskService.getTasksAssignedToUser(authentication.getName());
            return ResponseHandler.buildResponse("User tasks retrieved successfully", HttpStatus.OK, tasks);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error retrieving user tasks", HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}