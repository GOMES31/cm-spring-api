package dev.edugomes.springapi.auth;

import dev.edugomes.springapi.auth.request.LoginRequest;
import dev.edugomes.springapi.auth.request.RegisterRequest;
import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.auth.response.AuthResponse;
import dev.edugomes.springapi.util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private static final String REGISTER_USER = "/register";
    private static final String AUTHENTICATE_USER = "/login";


    @PostMapping(
            value = REGISTER_USER,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<AuthResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
        AuthResponse authResponse = authService.registerUser(registerRequest);
        return ResponseHandler.buildResponse("User registered successfully", HttpStatus.CREATED, authResponse);
    }

    @PostMapping(
            value = AUTHENTICATE_USER,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        AuthResponse authResponse = authService.authenticateUser(loginRequest);
        return ResponseHandler.buildResponse("User authenticated successfully", HttpStatus.OK, authResponse);
    }


}
