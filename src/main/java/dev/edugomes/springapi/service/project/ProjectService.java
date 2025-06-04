package dev.edugomes.springapi.service.project;

import dev.edugomes.springapi.dto.request.CreateProjectRequest;
import dev.edugomes.springapi.dto.request.UpdateProjectRequest;
import dev.edugomes.springapi.dto.response.ProjectResponse;
import dev.edugomes.springapi.dto.response.TaskResponse;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest createProjectRequest, String userEmail);

    ProjectResponse updateProject(Long id, UpdateProjectRequest updateProjectRequest, String userEmail);

    ProjectResponse getProjectById(Long id, String userEmail);

    List<TaskResponse> getTasksForProject(Long projectId, String userEmail);
}