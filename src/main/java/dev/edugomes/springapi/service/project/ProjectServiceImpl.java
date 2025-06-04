package dev.edugomes.springapi.service.project;

import dev.edugomes.springapi.domain.*;
import dev.edugomes.springapi.dto.request.CreateProjectRequest;
import dev.edugomes.springapi.dto.request.UpdateProjectRequest;
import dev.edugomes.springapi.dto.response.ProjectResponse;
import dev.edugomes.springapi.dto.response.TaskResponse;
import dev.edugomes.springapi.exception.ProjectNotFoundException;
import dev.edugomes.springapi.exception.UnauthorizedException;
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
        return convertToProjectResponse(savedProject);
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

        return convertToProjectResponse(project);
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
        return convertToProjectResponse(updatedProject);
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
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    private ProjectResponse convertToProjectResponse(Project project) {
        List<ProjectResponse.TaskInfo> taskInfos = project.getTasks().stream()
                .map(task -> ProjectResponse.TaskInfo.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .startDate(task.getStartDate())
                        .endDate(task.getEndDate())
                        .assigneeCount(task.getAssignees().size())
                        .build())
                .collect(Collectors.toList());

        ProjectResponse.TeamInfo teamInfo = ProjectResponse.TeamInfo.builder()
                .id(project.getTeam().getId())
                .name(project.getTeam().getName())
                .department(project.getTeam().getDepartment())
                .build();

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus())
                .team(teamInfo)
                .taskCount(project.getTasks().size())
                .tasks(taskInfos)
                .build();
    }

    private TaskResponse convertToTaskResponse(ProjectTask task) {
        TaskResponse.ProjectInfo projectInfo = TaskResponse.ProjectInfo.builder()
                .id(task.getProject().getId())
                .name(task.getProject().getName())
                .description(task.getProject().getDescription())
                .status(task.getProject().getStatus())
                .build();

        List<TaskResponse.AssigneeInfo> assigneesInfo = task.getAssignees().stream()
                .map(assignee -> TaskResponse.AssigneeInfo.builder()
                        .id(assignee.getId())
                        .name(assignee.getUser().getName())
                        .email(assignee.getUser().getEmail())
                        .teamRole(assignee.getRole().name())
                        .avatarUrl(assignee.getUser().getAvatarUrl())
                        .build())
                .collect(Collectors.toList());

        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .project(projectInfo)
                .assignees(assigneesInfo)
                .observationCount(task.getObservations() != null ? task.getObservations().size() : 0)
                .build();
    }
}