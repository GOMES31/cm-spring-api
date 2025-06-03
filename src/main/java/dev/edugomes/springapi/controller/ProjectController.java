package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.request.CreateProjectRequest;
import dev.edugomes.springapi.dto.request.UpdateProjectRequest;
import dev.edugomes.springapi.dto.response.ProjectResponse;
import dev.edugomes.springapi.exception.ProjectNotFoundException;
import dev.edugomes.springapi.exception.UnauthorizedException;
import dev.edugomes.springapi.service.project.ProjectService;
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
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    private static final String CREATE_PROJECT = "";
    private static final String GET_ALL_PROJECTS = "";
    private static final String GET_PROJECT_BY_ID = "/{id}";
    private static final String UPDATE_PROJECT = "/{id}";
    private static final String DELETE_PROJECT = "/{id}";
    private static final String GET_PROJECTS_BY_TEAM = "/team/{teamId}";

    @PostMapping(
            value = CREATE_PROJECT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
            @Valid @RequestBody CreateProjectRequest createProjectRequest,
            Authentication authentication) {
        try {
            ProjectResponse projectResponse = projectService.createProject(createProjectRequest, authentication.getName());
            return ResponseHandler.buildResponse("Project created successfully", HttpStatus.CREATED, projectResponse);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to create project", HttpStatus.UNAUTHORIZED, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error creating project: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }

    @GetMapping(
            value = GET_ALL_PROJECTS,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getAllProjects(Authentication authentication) {
        try {
            List<ProjectResponse> projects = projectService.getAllProjectsByUser(authentication.getName());
            return ResponseHandler.buildResponse("Projects retrieved successfully", HttpStatus.OK, projects);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Error retrieving projects: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @GetMapping(
            value = GET_PROJECT_BY_ID,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ProjectResponse>> getProjectById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            ProjectResponse projectResponse = projectService.getProjectById(id, authentication.getName());
            return ResponseHandler.buildResponse("Project retrieved successfully", HttpStatus.OK, projectResponse);
        } catch (ProjectNotFoundException e) {
            return ResponseHandler.buildResponse("Project not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to access project", HttpStatus.UNAUTHORIZED, null);
        }
    }

    @PutMapping(
            value = UPDATE_PROJECT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest updateProjectRequest,
            Authentication authentication) {
        try {
            ProjectResponse projectResponse = projectService.updateProject(id, updateProjectRequest, authentication.getName());
            return ResponseHandler.buildResponse("Project updated successfully", HttpStatus.OK, projectResponse);
        } catch (ProjectNotFoundException e) {
            return ResponseHandler.buildResponse("Project not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to update project", HttpStatus.UNAUTHORIZED, null);
        }
    }

    @DeleteMapping(
            value = DELETE_PROJECT,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            projectService.deleteProject(id, authentication.getName());
            return ResponseHandler.buildResponse("Project deleted successfully", HttpStatus.OK, null);
        } catch (ProjectNotFoundException e) {
            return ResponseHandler.buildResponse("Project not found", HttpStatus.NOT_FOUND, null);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to delete project", HttpStatus.UNAUTHORIZED, null);
        }
    }

    @GetMapping(
            value = GET_PROJECTS_BY_TEAM,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjectsByTeam(
            @PathVariable Long teamId,
            Authentication authentication) {
        try {
            List<ProjectResponse> projects = projectService.getProjectsByTeam(teamId, authentication.getName());
            return ResponseHandler.buildResponse("Team projects retrieved successfully", HttpStatus.OK, projects);
        } catch (UnauthorizedException e) {
            return ResponseHandler.buildResponse("Unauthorized to access team projects", HttpStatus.UNAUTHORIZED, null);
        }
    }
}