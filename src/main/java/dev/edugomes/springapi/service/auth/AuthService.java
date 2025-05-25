package dev.edugomes.springapi.service.auth;

import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.dto.request.SignInRequest;
import dev.edugomes.springapi.dto.request.SignUpRequest;
import dev.edugomes.springapi.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

public interface AuthService {

    void revokeAllUserTokens(User user);

    void saveUserToken(User user, String jwtToken);

    AuthResponse registerUser(SignUpRequest signUpRequest);

    AuthResponse authenticateUser(SignInRequest signInRequest);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;

}
