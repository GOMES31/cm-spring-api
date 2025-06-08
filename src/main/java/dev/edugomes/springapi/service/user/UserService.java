package dev.edugomes.springapi.service.user;

import dev.edugomes.springapi.dto.request.UpdateUserProfileRequest;
import dev.edugomes.springapi.dto.response.*;

import java.util.List;

public interface UserService {

    UserProfileResponse updateProfile(UpdateUserProfileRequest request);

    List<TeamResponse> getTeams();

    List<TaskResponse> getTasksForAuthenticatedUser();

    List<ProjectResponse> getProjectsForAuthenticatedUser();

    List<ObservationResponse> getObservationsForAuthenticatedUser();
}