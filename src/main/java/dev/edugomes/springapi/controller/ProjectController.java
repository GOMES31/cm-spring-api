package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.request.CreateProjectRequest;
import dev.edugomes.springapi.dto.request.UpdateProjectRequest;
import dev.edugomes.springapi.dto.response.ProjectResponse;
import dev.edugomes.springapi.dto.response.TaskResponse;
import dev.edugomes.springapi.exception.ProjectNotFoundException;
import dev.edugomes.springapi.exception.UnauthorizedException;
import dev.edugomes.springapi.service.project.ProjectService;
import dev.edugomes.springapi.util.ResponseHandler;
import dev.edugomes.springapi.util.GlobalMethods;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    private static final String CREATE_PROJECT = "/create";
    private static final String UPDATE_PROJECT = "/update";
    private static final String GET_PROJECT_BY_ID = "/{id}";
    private static final String GET_TASKS_BY_PROJECT_ID = "/{projectId}/tasks";

    @PostMapping(
            value = CREATE_PROJECT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest createProjectRequest) {
        try {
            String userEmail = GlobalMethods.getCurrentUserEmail();
            ProjectResponse projectResponse = projectService.createProject(createProjectRequest, userEmail);
            return ResponseHandler.buildResponse("Project created successfully", HttpStatus.CREATED, projectResponse);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to create project", HttpStatus.UNAUTHORIZED, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error creating project: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @PutMapping(
            value = UPDATE_PROJECT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @Valid @RequestBody UpdateProjectRequest updateProjectRequest) {
        try {
            if (updateProjectRequest.getId() == null) {
                return ResponseHandler.buildResponse("Project ID is required for update", HttpStatus.BAD_REQUEST, null);
            }
            String userEmail = GlobalMethods.getCurrentUserEmail();
            ProjectResponse projectResponse = projectService.updateProject(updateProjectRequest.getId(), updateProjectRequest, userEmail);
            return ResponseHandler.buildResponse("Project updated successfully", HttpStatus.OK, projectResponse);
        } catch (ProjectNotFoundException e) {
            return ResponseHandler.buildResponse("Project not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to update project", HttpStatus.UNAUTHORIZED, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error updating project: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping(
            value = GET_PROJECT_BY_ID,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(
            @PathVariable Long id) {
        try {
            String userEmail = GlobalMethods.getCurrentUserEmail();
            ProjectResponse projectResponse = projectService.getProjectById(id, userEmail);
            return ResponseHandler.buildResponse("Project retrieved successfully", HttpStatus.OK, projectResponse);
        } catch (ProjectNotFoundException e) {
            return ResponseHandler.buildResponse("Project not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to access project", HttpStatus.UNAUTHORIZED, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error retrieving project: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping(
            value = GET_TASKS_BY_PROJECT_ID,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksForProject(
            @PathVariable Long projectId) {
        try {
            String userEmail = GlobalMethods.getCurrentUserEmail();
            List<TaskResponse> tasks = projectService.getTasksForProject(projectId, userEmail);
            return ResponseHandler.buildResponse("Tasks retrieved successfully for project", HttpStatus.OK, tasks);
        } catch (ProjectNotFoundException e) {
            return ResponseHandler.buildResponse("Project not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to access tasks for this project", HttpStatus.UNAUTHORIZED, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error retrieving tasks for project: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}