package dev.edugomes.springapi.service.team;

import dev.edugomes.springapi.dto.request.CreateTeamRequest;
import dev.edugomes.springapi.dto.response.TeamResponse;


public interface TeamService {

    TeamResponse create(CreateTeamRequest request);
}
