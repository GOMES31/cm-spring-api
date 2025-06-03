package dev.edugomes.springapi.service.project;

import dev.edugomes.springapi.domain.*;
import dev.edugomes.springapi.dto.request.CreateProjectRequest;
import dev.edugomes.springapi.dto.request.UpdateProjectRequest;
import dev.edugomes.springapi.dto.response.ProjectResponse;
import dev.edugomes.springapi.exception.ProjectNotFoundException;
import dev.edugomes.springapi.exception.UnauthorizedException;
import dev.edugomes.springapi.repository.ProjectRepository;
import dev.edugomes.springapi.repository.TeamRepository;
import dev.edugomes.springapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
    public List<ProjectResponse> getAllProjectsByUser(String userEmail) {
        List<Project> projects = projectRepository.findProjectsByUserEmail(userEmail);
        return projects.stream()
                .map(this::convertToProjectResponse)
                .collect(Collectors.toList());
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
    public void deleteProject(Long id, String userEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));

        boolean canDelete = project.getTeam().getMembers().stream()
                .anyMatch(member -> member.getUser().getEmail().equals(userEmail) &&
                        member.getRole() == TeamRole.ADMIN);

        if (!canDelete) {
            throw new UnauthorizedException("User not authorized to delete this project");
        }

        projectRepository.delete(project);
    }

    @Override
    public List<ProjectResponse> getProjectsByTeam(Long teamId, String userEmail) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        boolean isMember = team.getMembers().stream()
                .anyMatch(member -> member.getUser().getEmail().equals(userEmail));

        if (!isMember) {
            throw new UnauthorizedException("User not authorized to access team projects");
        }

        List<Project> projects = projectRepository.findByTeamId(teamId);
        return projects.stream()
                .map(this::convertToProjectResponse)
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
                .description(project.getTeam().getDescription())
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
}