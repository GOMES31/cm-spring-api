package dev.edugomes.springapi.service.project;

import dev.edugomes.springapi.dto.request.CreateProjectRequest;
import dev.edugomes.springapi.dto.request.UpdateProjectRequest;
import dev.edugomes.springapi.dto.response.ProjectResponse;

import java.util.List;

public interface ProjectService {

    ProjectResponse createProject(CreateProjectRequest createProjectRequest, String userEmail);

    List<ProjectResponse> getAllProjectsByUser(String userEmail);

    ProjectResponse getProjectById(Long id, String userEmail);

    ProjectResponse updateProject(Long id, UpdateProjectRequest updateProjectRequest, String userEmail);

    void deleteProject(Long id, String userEmail);

    List<ProjectResponse> getProjectsByTeam(Long teamId, String userEmail);
}