package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.dto.request.SignInRequest;
import dev.edugomes.springapi.dto.request.SignUpRequest;
import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.response.AuthResponse;
import dev.edugomes.springapi.dto.response.RefreshResponse;
import dev.edugomes.springapi.exception.UserAlreadyExistsException;
import dev.edugomes.springapi.service.auth.AuthService;
import dev.edugomes.springapi.utils.ResponseHandler;
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

    private static final String REGISTER_USER = "/signup";
    private static final String AUTHENTICATE_USER = "/signin";
    private static final String REFRESH_TOKEN = "/refresh";


    @PostMapping(
            value = REGISTER_USER,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<AuthResponse>> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
        try {
            AuthResponse authResponse = authService.registerUser(signUpRequest);
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
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateUser(@Valid @RequestBody SignInRequest signInRequest){
        try{
            AuthResponse authResponse = authService.authenticateUser(signInRequest);
            return ResponseHandler.buildResponse("User authenticated successfully", HttpStatus.OK, authResponse);
        } catch( UsernameNotFoundException e){
            return ResponseHandler.buildResponse("User not found", HttpStatus.NOT_FOUND, null);
        } catch (BadCredentialsException e) {
            return ResponseHandler.buildResponse("Invalid credentials", HttpStatus.UNAUTHORIZED, null);
        }

    }

    @PostMapping(
            value = REFRESH_TOKEN,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<RefreshResponse>> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            RefreshResponse refreshResponse = authService.refreshToken(request);
            return ResponseHandler.buildResponse("Token refreshed successfully", HttpStatus.OK, refreshResponse);
        } catch(UsernameNotFoundException e){
            return ResponseHandler.buildResponse("User not found", HttpStatus.NOT_FOUND, null);
        } catch (Exception e){
            return ResponseHandler.buildResponse("Invalid refresh token", HttpStatus.UNAUTHORIZED, null);
        }
    }


}
