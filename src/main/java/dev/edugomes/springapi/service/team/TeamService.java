package dev.edugomes.springapi.service.team;

import dev.edugomes.springapi.dto.request.CreateTeamRequest;
import dev.edugomes.springapi.dto.request.AddTeamMemberRequest;
import dev.edugomes.springapi.dto.request.UpdateTeamMemberRequest;
import dev.edugomes.springapi.dto.request.UpdateTeamRequest;
import dev.edugomes.springapi.dto.response.TeamMemberResponse;
import dev.edugomes.springapi.dto.response.TeamResponse;

public interface TeamService {
    TeamResponse create(CreateTeamRequest request);

    TeamResponse getTeamById(Long teamId);


    TeamResponse updateTeam(Long teamId, UpdateTeamRequest request);

    TeamMemberResponse addMember(Long teamId, AddTeamMemberRequest request);

    void removeMember(Long teamId, Long memberId);

    TeamMemberResponse updateMember(Long teamId, Long memberId, UpdateTeamMemberRequest request);
}
