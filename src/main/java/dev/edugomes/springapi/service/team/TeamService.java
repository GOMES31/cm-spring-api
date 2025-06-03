package dev.edugomes.springapi.service.team;

import dev.edugomes.springapi.dto.request.CreateTeamRequest;
import dev.edugomes.springapi.dto.request.AddTeamMemberRequest;
import dev.edugomes.springapi.dto.request.UpdateTeamRequest;
import dev.edugomes.springapi.dto.response.TeamResponse;

public interface TeamService {
    TeamResponse create(CreateTeamRequest request);

    TeamResponse getTeamById(Long teamId);

    void addMember(Long teamId, AddTeamMemberRequest request);

    TeamResponse updateTeam(Long teamId, UpdateTeamRequest request);

    void removeMember(Long teamId, Long memberId);
}
