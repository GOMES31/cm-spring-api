package dev.edugomes.springapi.mapper;

import dev.edugomes.springapi.domain.Team;
import dev.edugomes.springapi.domain.TeamMember;
import dev.edugomes.springapi.dto.response.TeamMemberResponse;
import dev.edugomes.springapi.dto.response.TeamResponse;
import lombok.experimental.UtilityClass;
import java.util.stream.Collectors;

@UtilityClass
public class Mapper {

    public TeamResponse toTeamResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .department(team.getDepartment())
                .imageUrl(team.getImageUrl())
                .members(team.getMembers().stream()
                        .map(Mapper::toTeamMemberResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public TeamMemberResponse toTeamMemberResponse(TeamMember member) {
        return TeamMemberResponse.builder()
                .id(member.getId())
                .name(member.getUser().getName())
                .email(member.getUser().getEmail())
                .role(member.getRole().toString())
                .build();
    }
}
