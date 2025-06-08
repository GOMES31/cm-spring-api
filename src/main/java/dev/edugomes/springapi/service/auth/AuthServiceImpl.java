package dev.edugomes.springapi.service.auth;

import dev.edugomes.springapi.domain.Token;
import dev.edugomes.springapi.domain.TokenType;
import dev.edugomes.springapi.dto.request.SignInRequest;
import dev.edugomes.springapi.dto.request.SignUpRequest;
import dev.edugomes.springapi.exception.UserAlreadyExistsException;
import dev.edugomes.springapi.jwt.JwtService;
import dev.edugomes.springapi.domain.Role;
import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.dto.response.AuthResponse;
import dev.edugomes.springapi.dto.response.RefreshResponse;
import dev.edugomes.springapi.mapper.CustomMapper;
import dev.edugomes.springapi.repository.TokenRepository;
import dev.edugomes.springapi.repository.UserRepository;
import dev.edugomes.springapi.service.log.LogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final LogService logService;
    private final AuthenticationManager authenticationManager;


    @Override
    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());

        if(validUserTokens.isEmpty()) {
            return;
        }

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    public void saveRefreshToken(User user, String refreshToken) {
        var token = Token.builder()
                .user(user)
                .token(refreshToken)
                .type(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();

        tokenRepository.save(token);
    }

    @Override
    public AuthResponse registerUser(SignUpRequest signUpRequest) {

        if(userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }

        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signUpRequest.getEmail(),
                        signUpRequest.getPassword()
                )
        );

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(savedUser, refreshToken);

        logService.saveLog("Sign Up", savedUser.getEmail());

        return CustomMapper.toAuthResponse(user, accessToken, refreshToken);
    }



    public AuthResponse authenticateUser(SignInRequest signInRequest) {

        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + signInRequest.getEmail()));

        // Verify if the provided password matches the stored password
        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getEmail(),
                        signInRequest.getPassword()
                )
        );

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveRefreshToken(user, refreshToken);

        logService.saveLog("Sign In", user.getEmail());

        return CustomMapper.toAuthResponse(user, accessToken, refreshToken);
    }

    @Override
    public RefreshResponse refreshToken(HttpServletRequest request) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }

        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new IllegalArgumentException("Could not extract email from token");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        var storedToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Token not found in database"));

        if (storedToken.isExpired() || storedToken.isRevoked()) {
            throw new RuntimeException("Token is expired or revoked");
        }

        boolean isTokenValid = jwtService.isTokenValid(refreshToken, user);

        if(!isTokenValid) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveRefreshToken(user, newRefreshToken);

        // Verify if the new refresh token was saved correctly
        tokenRepository.findByToken(newRefreshToken)
                .orElseThrow(() -> new RuntimeException("New refresh token not found in database after saving"));

        return CustomMapper.toRefreshResponse(newAccessToken, newRefreshToken);
    }
}
