package dev.edugomes.springapi.service.user;

import dev.edugomes.springapi.dto.request.UpdateUserProfileRequest;
import dev.edugomes.springapi.dto.response.UpdateUserProfileResponse;

public interface UserService {
    UpdateUserProfileResponse updateProfile(UpdateUserProfileRequest request);
}
