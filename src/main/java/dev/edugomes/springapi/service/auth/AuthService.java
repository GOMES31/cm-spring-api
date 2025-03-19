package dev.edugomes.springapi.service.auth;

import dev.edugomes.springapi.dto.request.LoginRequest;
import dev.edugomes.springapi.dto.request.RegisterRequest;
import dev.edugomes.springapi.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthService {

    AuthResponse registerUser(RegisterRequest registerRequest);

    AuthResponse authenticateUser(LoginRequest loginRequest);

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
