package dev.edugomes.springapi.service.user;
import dev.edugomes.springapi.dto.request.UpdateUserProfileRequest;
import dev.edugomes.springapi.dto.response.UpdateUserProfileResponse;
import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.exception.UserNotFoundException;
import dev.edugomes.springapi.jwt.JwtService;
import dev.edugomes.springapi.repository.UserRepository;
import dev.edugomes.springapi.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthService authService;
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

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        authService.revokeAllUserTokens(user);
        authService.saveUserToken(user, accessToken);
        userRepository.save(user);

        return UpdateUserProfileResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
