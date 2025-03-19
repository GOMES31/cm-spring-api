package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.dto.request.LoginRequest;
import dev.edugomes.springapi.dto.request.RegisterRequest;
import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.response.AuthResponse;
import dev.edugomes.springapi.exception.UserAlreadyExistsException;
import dev.edugomes.springapi.jwt.JwtService;
import dev.edugomes.springapi.service.auth.AuthService;
import dev.edugomes.springapi.util.ResponseHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private static final String REGISTER_USER = "/register";
    private static final String AUTHENTICATE_USER = "/login";
    private static final String REFRESH_TOKEN = "/refresh";
    private final JwtService jwtService;


    @PostMapping(
            value = REGISTER_USER,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<AuthResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest){
        try {
            AuthResponse authResponse = authService.registerUser(registerRequest);
            return ResponseHandler.buildResponse("User registered successfully", HttpStatus.CREATED, authResponse);
        } catch( UserAlreadyExistsException e){
            return ResponseHandler.buildResponse("User already exists",HttpStatus.CONFLICT, null);
        }
    }

    @PostMapping(
            value = AUTHENTICATE_USER,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        try{
            AuthResponse authResponse = authService.authenticateUser(loginRequest);
            return ResponseHandler.buildResponse("User authenticated successfully", HttpStatus.OK, authResponse);
        } catch( UsernameNotFoundException e){
            return ResponseHandler.buildResponse("User not found", HttpStatus.NOT_FOUND, null);
        } catch (BadCredentialsException e) {
            return ResponseHandler.buildResponse("Invalid credentials", HttpStatus.UNAUTHORIZED, null);
        }

    }

    @PostMapping(
            value = REFRESH_TOKEN,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authService.refreshToken(request,response);
    }


}
