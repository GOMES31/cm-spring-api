package dev.edugomes.springapi.service.user;

import dev.edugomes.springapi.domain.Team;
import dev.edugomes.springapi.dto.request.UpdateUserProfileRequest;
import dev.edugomes.springapi.dto.response.TeamResponse;
import dev.edugomes.springapi.dto.response.UserProfileResponse;
import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.exception.UserNotFoundException;
import dev.edugomes.springapi.mapper.CustomMapper;
import dev.edugomes.springapi.repository.UserRepository;
import dev.edugomes.springapi.service.log.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static dev.edugomes.springapi.util.GlobalMethods.getCurrentUserEmail;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final LogService logService;

    @Override
    public UserProfileResponse updateProfile(UpdateUserProfileRequest request) {
        String email = getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }


        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        userRepository.save(user);

        logService.saveLog("Update user profile",email);

        return CustomMapper.toUserProfileResponse(user);
    }

    @Override
    public List<TeamResponse> getTeams() {
        String email = getCurrentUserEmail();

        if(email == null || email.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        logService.saveLog("Get User Teams", email);

        List<Team> teams = userRepository.findTeamsByUserEmail(email);
        return teams.stream()
                .map(CustomMapper::toTeamResponse)
                .toList();
    }


}

