package dev.edugomes.springapi.service.user;

import dev.edugomes.springapi.domain.Team;
import dev.edugomes.springapi.dto.request.UpdateUserProfileRequest;
import dev.edugomes.springapi.dto.response.UpdateUserProfileResponse;

import java.util.List;

public interface UserService {
    UpdateUserProfileResponse updateProfile(UpdateUserProfileRequest request);

    List<Team> getTeams();
}