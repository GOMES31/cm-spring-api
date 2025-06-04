package dev.edugomes.springapi.mapper;

import dev.edugomes.springapi.domain.Team;
import dev.edugomes.springapi.domain.TeamMember;
import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.dto.response.AuthResponse;
import dev.edugomes.springapi.dto.response.TeamMemberResponse;
import dev.edugomes.springapi.dto.response.TeamResponse;
import dev.edugomes.springapi.dto.response.UserProfileResponse;
import lombok.experimental.UtilityClass;
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
}
