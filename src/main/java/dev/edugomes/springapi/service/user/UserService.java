package dev.edugomes.springapi.service.user;

import dev.edugomes.springapi.dto.request.UpdateUserProfileRequest;
import dev.edugomes.springapi.dto.response.TeamResponse;
import dev.edugomes.springapi.dto.response.UserProfileResponse;

import java.util.List;

public interface UserService {
  
    UserProfileResponse updateProfile(UpdateUserProfileRequest request);

   List<TeamResponse> getTeams();
}