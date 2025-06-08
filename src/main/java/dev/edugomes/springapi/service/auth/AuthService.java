package dev.edugomes.springapi.service.auth;

import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.dto.request.SignInRequest;
import dev.edugomes.springapi.dto.request.SignUpRequest;
import dev.edugomes.springapi.dto.response.AuthResponse;
import dev.edugomes.springapi.dto.response.RefreshResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Map;

public interface AuthService {

    void revokeAllUserTokens(User user);

    void saveRefreshToken(User user, String refreshToken);

    AuthResponse registerUser(SignUpRequest signUpRequest);

    AuthResponse authenticateUser(SignInRequest signInRequest);

    RefreshResponse refreshToken(HttpServletRequest request) throws IOException;

}
