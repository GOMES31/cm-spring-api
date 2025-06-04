package dev.edugomes.springapi.service.user;
import dev.edugomes.springapi.domain.Team;
import dev.edugomes.springapi.dto.request.UpdateUserProfileRequest;
import dev.edugomes.springapi.dto.response.UpdateUserProfileResponse;
import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.exception.UserNotFoundException;
import dev.edugomes.springapi.repository.UserRepository;
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



    @Override
    public UpdateUserProfileResponse updateProfile(UpdateUserProfileRequest request) {


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

        return UpdateUserProfileResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    @Override
    public List<Team> getTeams() {
        String email = getCurrentUserEmail();

        if(email == null || email.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        return userRepository.findTeamsByUserEmail(email);
    }


}
