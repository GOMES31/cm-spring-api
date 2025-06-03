package dev.edugomes.springapi.service.task;

import dev.edugomes.springapi.domain.*;
import dev.edugomes.springapi.dto.request.CreateTaskRequest;
import dev.edugomes.springapi.dto.request.UpdateTaskRequest;
import dev.edugomes.springapi.dto.response.TaskResponse;
import dev.edugomes.springapi.exception.ProjectNotFoundException;
import dev.edugomes.springapi.exception.TaskNotFoundException;
import dev.edugomes.springapi.exception.TeamMemberNotFoundException;
import dev.edugomes.springapi.exception.UnauthorizedException;
import dev.edugomes.springapi.repository.ProjectRepository;
import dev.edugomes.springapi.repository.TaskRepository;
import dev.edugomes.springapi.repository.TeamMemberRepository;
import dev.edugomes.springapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    @Override
    public TaskResponse createTask(CreateTaskRequest createTaskRequest, String userEmail) {
        Project project = projectRepository.findById(createTaskRequest.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        boolean hasAccess = project.getTeam().getMembers().stream()
                .anyMatch(member -> member.getUser().getEmail().equals(userEmail));

        if (!hasAccess) {
            throw new UnauthorizedException("User does not have access to this project");
        }

        ProjectTask task = ProjectTask.builder()
                .title(createTaskRequest.getTitle())
                .description(createTaskRequest.getDescription())
                .status(createTaskRequest.getStatus() != null ? createTaskRequest.getStatus() : Status.PENDING)
                .endDate(createTaskRequest.getEndDate())
                .project(project)
                .assignees(new ArrayList<>())
                .observations(new ArrayList<>())
                .build();

        if (createTaskRequest.getAssigneeIds() != null && !createTaskRequest.getAssigneeIds().isEmpty()) {
            List<TeamMember> assignees = new ArrayList<>();
            for (Long assigneeId : createTaskRequest.getAssigneeIds()) {
                TeamMember teamMember = teamMemberRepository.findById(assigneeId)
                        .orElseThrow(() -> new TeamMemberNotFoundException("Team member not found"));

                if (!teamMember.getTeam().getId().equals(project.getTeam().getId())) {
                    throw new UnauthorizedException("Team member does not belong to the project's team");
                }

                assignees.add(teamMember);
            }
            task.setAssignees(assignees);
        }

        ProjectTask savedTask = taskRepository.save(task);
        return mapToTaskResponse(savedTask);
    }

    @Override
    public TaskResponse getTaskById(Long id, String userEmail) {
        ProjectTask task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        boolean hasAccess = task.getProject().getTeam().getMembers().stream()
                .anyMatch(member -> member.getUser().getEmail().equals(userEmail));

        if (!hasAccess) {
            throw new UnauthorizedException("User does not have access to this task");
        }

        return mapToTaskResponse(task);
    }

    @Override
    public List<TaskResponse> getAllTasksForUser(String userEmail) {
        List<ProjectTask> tasks = taskRepository.findTasksForUserTeams(userEmail);
        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksByProject(Long projectId, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        boolean hasAccess = project.getTeam().getMembers().stream()
                .anyMatch(member -> member.getUser().getEmail().equals(userEmail));

        if (!hasAccess) {
            throw new UnauthorizedException("User does not have access to this project");
        }

        List<ProjectTask> tasks = taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksAssignedToUser(String userEmail) {
        List<ProjectTask> tasks = taskRepository.findTasksAssignedToUser(userEmail);
        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TaskResponse updateTask(Long id, UpdateTaskRequest updateTaskRequest, String userEmail) {
        ProjectTask task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        boolean hasAccess = task.getProject().getTeam().getMembers().stream()
                .anyMatch(member -> member.getUser().getEmail().equals(userEmail));

        if (!hasAccess) {
            throw new UnauthorizedException("User does not have access to this task");
        }

        if (updateTaskRequest.getTitle() != null) {
            task.setTitle(updateTaskRequest.getTitle());
        }
        if (updateTaskRequest.getDescription() != null) {
            task.setDescription(updateTaskRequest.getDescription());
        }
        if (updateTaskRequest.getStatus() != null) {
            task.setStatus(updateTaskRequest.getStatus());
        }
        if (updateTaskRequest.getEndDate() != null) {
            task.setEndDate(updateTaskRequest.getEndDate());
        }

        if (updateTaskRequest.getAssigneeIds() != null) {
            List<TeamMember> newAssignees = new ArrayList<>();
            for (Long assigneeId : updateTaskRequest.getAssigneeIds()) {
                TeamMember teamMember = teamMemberRepository.findById(assigneeId)
                        .orElseThrow(() -> new TeamMemberNotFoundException("Team member not found"));

                if (!teamMember.getTeam().getId().equals(task.getProject().getTeam().getId())) {
                    throw new UnauthorizedException("Team member does not belong to the project's team");
                }

                newAssignees.add(teamMember);
            }
            task.setAssignees(newAssignees);
        }

        ProjectTask updatedTask = taskRepository.save(task);
        return mapToTaskResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long id, String userEmail) {
        ProjectTask task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        boolean hasAccess = task.getProject().getTeam().getMembers().stream()
                .anyMatch(member -> member.getUser().getEmail().equals(userEmail));

        if (!hasAccess) {
            throw new UnauthorizedException("User does not have access to this task");
        }

        taskRepository.delete(task);
    }

    private TaskResponse mapToTaskResponse(ProjectTask task) {
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
                        .profilePicture(assignee.getUser().getProfilePicture())
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
