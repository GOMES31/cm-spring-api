package dev.edugomes.springapi.service.project;

import dev.edugomes.springapi.domain.Project;
import dev.edugomes.springapi.domain.Team;
import dev.edugomes.springapi.domain.TeamRole;
import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.dto.request.CreateProjectRequest;
import dev.edugomes.springapi.dto.request.UpdateProjectRequest;
import dev.edugomes.springapi.dto.response.ProjectResponse;
import dev.edugomes.springapi.dto.response.TaskResponse;
import dev.edugomes.springapi.exception.ProjectNotFoundException;
import dev.edugomes.springapi.exception.UnauthorizedException;
import dev.edugomes.springapi.mapper.CustomMapper;
import dev.edugomes.springapi.repository.ProjectRepository;
import dev.edugomes.springapi.repository.TeamRepository;
import dev.edugomes.springapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Override
    public ProjectResponse createProject(CreateProjectRequest createProjectRequest, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        Team team = teamRepository.findById(createProjectRequest.getTeamId())
                .orElseThrow(() -> new RuntimeException("Team not found"));

        boolean isAuthorized = team.getMembers().stream()
                .anyMatch(member -> member.getUser().getEmail().equals(userEmail) &&
                        (member.getRole() == TeamRole.ADMIN || member.getRole() == TeamRole.PROJECT_MANAGER));

        if (!isAuthorized) {
            throw new UnauthorizedException("User not authorized to create projects for this team");
        }

        Project project = Project.builder()
                .name(createProjectRequest.getName())
                .description(createProjectRequest.getDescription())
                .startDate(createProjectRequest.getStartDate())
                .endDate(createProjectRequest.getEndDate())
                .status(createProjectRequest.getStatus())
                .team(team)
                .build();

        Project savedProject = projectRepository.save(project);
        return CustomMapper.toProjectResponse(savedProject);
    }

    @Override
    public ProjectResponse getProjectById(Long id, String userEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

        boolean hasAccess = project.getTeam().getMembers().stream()
                .anyMatch(member -> member.getUser().getEmail().equals(userEmail));

        if (!hasAccess) {
            throw new UnauthorizedException("User not authorized to access this project");
        }

        return CustomMapper.toProjectResponse(project);
    }

    @Override
    public ProjectResponse updateProject(Long id, UpdateProjectRequest updateProjectRequest, String userEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

        boolean canUpdate = project.getTeam().getMembers().stream()
                .anyMatch(member -> member.getUser().getEmail().equals(userEmail) &&
                        (member.getRole() == TeamRole.ADMIN || member.getRole() == TeamRole.PROJECT_MANAGER));

        if (!canUpdate) {
            throw new UnauthorizedException("User not authorized to update this project");
        }

        if (updateProjectRequest.getName() != null) {
            project.setName(updateProjectRequest.getName());
        }
        if (updateProjectRequest.getDescription() != null) {
            project.setDescription(updateProjectRequest.getDescription());
        }
        if (updateProjectRequest.getStartDate() != null) {
            project.setStartDate(updateProjectRequest.getStartDate());
        }
        if (updateProjectRequest.getEndDate() != null) {
            project.setEndDate(updateProjectRequest.getEndDate());
        }
        if (updateProjectRequest.getStatus() != null) {
            project.setStatus(updateProjectRequest.getStatus());
        }

        Project updatedProject = projectRepository.save(project);
        return CustomMapper.toProjectResponse(updatedProject);
    }

    @Override
    public List<TaskResponse> getTasksForProject(Long projectId, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        boolean hasAccess = project.getTeam().getMembers().stream()
                .anyMatch(member -> member.getUser().getEmail().equals(userEmail));

        if (!hasAccess) {
            throw new UnauthorizedException("User not authorized to access tasks for this project");
        }

        return project.getTasks().stream()
                .map(CustomMapper::toTaskResponse)
                .collect(Collectors.toList());
    }
}