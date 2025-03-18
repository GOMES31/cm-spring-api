package dev.edugomes.springapi.auth;

import dev.edugomes.springapi.auth.request.LoginRequest;
import dev.edugomes.springapi.auth.request.RegisterRequest;
import dev.edugomes.springapi.auth.response.AuthResponse;

public interface AuthService {

    AuthResponse registerUser(RegisterRequest registerRequest);

    AuthResponse authenticateUser(LoginRequest loginRequest);
}
