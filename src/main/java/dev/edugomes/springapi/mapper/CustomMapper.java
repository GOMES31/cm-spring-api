package dev.edugomes.springapi.mapper;

import dev.edugomes.springapi.domain.*;
import dev.edugomes.springapi.dto.response.*;
import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.stream.Collectors;


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
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    public AuthResponse toAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public RefreshResponse toRefreshResponse(String accessToken, String refreshToken) {
        return RefreshResponse.builder()
                .accessToken(accessToken)
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
                .imageUrl(observation.getImageUrl())
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
