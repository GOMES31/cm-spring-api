package dev.edugomes.springapi.mapper;

import dev.edugomes.springapi.domain.Team;
import dev.edugomes.springapi.domain.TeamMember;
import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.domain.Project;
import dev.edugomes.springapi.domain.Task;
import dev.edugomes.springapi.domain.Observation;
import dev.edugomes.springapi.dto.response.ObservationResponse;
import dev.edugomes.springapi.dto.response.TaskResponse;
import dev.edugomes.springapi.dto.response.ProjectResponse;
import dev.edugomes.springapi.dto.response.AuthResponse;
import dev.edugomes.springapi.dto.response.TeamMemberResponse;
import dev.edugomes.springapi.dto.response.TeamResponse;
import dev.edugomes.springapi.dto.response.UserProfileResponse;
import lombok.experimental.UtilityClass;
import java.util.stream.Collectors;
import java.util.Optional;


@UtilityClass
public class CustomMapper {

    public TeamResponse toTeamResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .department(team.getDepartment())
                .imageUrl(team.getImageUrl())
                .members(team.getMembers().stream()
                        .map(CustomMapper::toTeamMemberResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public TeamMemberResponse toTeamMemberResponse(TeamMember member) {
        return TeamMemberResponse.builder()
                .id(member.getId())
                .name(member.getUser().getName())
                .email(member.getUser().getEmail())
                .avatarUrl(member.getUser().getAvatarUrl())
                .role(member.getRole().toString())
                .build();
    }

    public UserProfileResponse toUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    public AuthResponse toAuthResponse(User user, String access, String refreshToken) {
        return AuthResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .accessToken(access)
                .refreshToken(refreshToken)
                .build();
    }

    public ProjectResponse toProjectResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus())
                .team(ProjectResponse.TeamInfo.builder()
                        .id(project.getTeam().getId())
                        .name(project.getTeam().getName())
                        .department(project.getTeam().getDepartment())
                        .build())
                .tasks(project.getTasks().stream()
                        .map(task -> ProjectResponse.TaskInfo.builder()
                                .id(task.getId())
                                .title(task.getTitle())
                                .description(task.getDescription())
                                .status(task.getStatus())
                                .startDate(task.getStartDate())
                                .endDate(task.getEndDate())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    public TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .startDate(task.getStartDate())
                .endDate(task.getEndDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .project(TaskResponse.ProjectInfo.builder()
                        .id(task.getProject().getId())
                        .name(task.getProject().getName())
                        .description(task.getProject().getDescription())
                        .status(task.getProject().getStatus())
                        .build())
                .assignees(task.getAssignees().stream()
                        .map(assignee -> TaskResponse.TeamMemberInfo.builder()
                                .id(assignee.getId())
                                .name(assignee.getUser().getName())
                                .email(assignee.getUser().getEmail())
                                .teamRole(assignee.getRole().toString())
                                .avatarUrl(assignee.getUser().getAvatarUrl())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public ObservationResponse toObservationResponse(Observation observation) {
        return ObservationResponse.builder()
                .id(observation.getId())
                .message(observation.getMessage())
                .createdAt(observation.getCreatedAt())
                .task(Optional.ofNullable(observation.getTask())
                        .map(task -> ObservationResponse.TaskInfo.builder()
                                .id(task.getId())
                                .title(task.getTitle())
                                .build())
                        .orElse(null))
                .user(Optional.ofNullable(observation.getUser())
                        .map(user -> ObservationResponse.UserInfo.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .build())
                        .orElse(null))
                .image(Optional.ofNullable(observation.getImage())
                        .map(image -> ObservationResponse.ImageInfo.builder()
                                .id(image.getId())
                                .imageUrl(image.getImageUrl())
                                .uploadedAt(image.getUploadedAt())
                                .build())
                        .orElse(null))
                .build();
    }

    public TaskResponse.ObservationInfo toObservationInfo(Observation observation) {
        return TaskResponse.ObservationInfo.builder()
                .id(observation.getId())
                .message(observation.getMessage())
                .createdAt(observation.getCreatedAt())
                .build();
    }
}
