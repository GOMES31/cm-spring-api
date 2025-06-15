package dev.edugomes.springapi.service.task;

import dev.edugomes.springapi.domain.Project;
import dev.edugomes.springapi.domain.Status;
import dev.edugomes.springapi.domain.Task;
import dev.edugomes.springapi.domain.TeamMember;
import dev.edugomes.springapi.dto.request.CreateTaskRequest;
import dev.edugomes.springapi.dto.request.UpdateTaskRequest;
import dev.edugomes.springapi.dto.response.TaskResponse;
import dev.edugomes.springapi.dto.response.TaskResponse.ObservationInfo;
import dev.edugomes.springapi.exception.ProjectNotFoundException;
import dev.edugomes.springapi.exception.TaskNotFoundException;
import dev.edugomes.springapi.exception.TeamMemberNotFoundException;
import dev.edugomes.springapi.exception.UnauthorizedException;
import dev.edugomes.springapi.mapper.CustomMapper;
import dev.edugomes.springapi.repository.ProjectRepository;
import dev.edugomes.springapi.repository.TaskRepository;
import dev.edugomes.springapi.repository.TeamMemberRepository;
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

    @Override
    public TaskResponse createTask(CreateTaskRequest createTaskRequest, String userEmail) {
        Project project =
                projectRepository.findById(createTaskRequest.getProjectId())
                        .orElseThrow(() -> new ProjectNotFoundException("Project not found"));

        boolean hasAccess = project.getTeam().getMembers().stream()
                .anyMatch(member -> member.getUser().getEmail().equals(userEmail));

        if (!hasAccess) {
            throw new UnauthorizedException("User does not have access to this project");
        }

        Task task = Task.builder()
                .title(createTaskRequest.getTitle())
                .description(createTaskRequest.getDescription())
                .status(createTaskRequest.getStatus() != null ?
                        createTaskRequest.getStatus() : Status.PENDING)
                .endDate(createTaskRequest.getEndDate())
                .project(project)
                .assignees(new ArrayList<>())
                .observations(new ArrayList<>())
                .build();

        if (createTaskRequest.getAssigneeIds() != null &&
                !createTaskRequest.getAssigneeIds().isEmpty()) {
            List<TeamMember> assignees = new ArrayList<>();
            for (Long assigneeId : createTaskRequest.getAssigneeIds()) {
                TeamMember teamMember =
                        teamMemberRepository.findById(assigneeId)
                                .orElseThrow(() -> new TeamMemberNotFoundException("Team member not found"));

                if
                (!teamMember.getTeam().getId().equals(project.getTeam().getId())) {
                    throw new UnauthorizedException("Team member does not belong to the project's team");
                }

                assignees.add(teamMember);
            }
            task.setAssignees(assignees);
        }

        Task savedTask = taskRepository.save(task);
        return CustomMapper.toTaskResponse(savedTask);
    }

    @Override
    public TaskResponse getTaskById(Long id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        boolean hasAccess =
                task.getProject().getTeam().getMembers().stream()
                        .anyMatch(member -> member.getUser().getEmail().equals(userEmail));

        if (!hasAccess) {
            throw new UnauthorizedException("User does not have access to this task");
        }

        return CustomMapper.toTaskResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long id, UpdateTaskRequest updateTaskRequest, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        boolean hasAccess =
                task.getProject().getTeam().getMembers().stream()
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
                TeamMember teamMember =
                        teamMemberRepository.findById(assigneeId)
                                .orElseThrow(() -> new TeamMemberNotFoundException("Team member not found"));

                if
                (!teamMember.getTeam().getId().equals(task.getProject().getTeam().getId())) {
                    throw new UnauthorizedException("Team member does not belong to the project's team");
                }

                newAssignees.add(teamMember);
            }
            task.setAssignees(newAssignees);
        }

        Task updatedTask = taskRepository.save(task);
        return CustomMapper.toTaskResponse(updatedTask);
    }

    @Override
    public List<ObservationInfo> getObservationsForTask(Long taskId, String userEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));

        boolean hasAccess =
                task.getProject().getTeam().getMembers().stream()
                        .anyMatch(member -> member.getUser().getEmail().equals(userEmail));

        if (!hasAccess) {
            throw new UnauthorizedException("User does not have access to this task's observations");
        }

        return task.getObservations().stream()
                .map(CustomMapper::toObservationInfo)
                .collect(Collectors.toList());
    }
}