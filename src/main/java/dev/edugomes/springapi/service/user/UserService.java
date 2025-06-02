package dev.edugomes.springapi.service.user;

import dev.edugomes.springapi.domain.Team;
import dev.edugomes.springapi.dto.request.UpdateUserProfileRequest;
import dev.edugomes.springapi.dto.response.UpdateUserProfileResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UpdateUserProfileResponse updateProfile(UpdateUserProfileRequest request);

   List<Team> getTeams();
}
