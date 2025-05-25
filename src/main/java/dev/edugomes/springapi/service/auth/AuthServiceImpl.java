package dev.edugomes.springapi.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.edugomes.springapi.domain.Token;
import dev.edugomes.springapi.domain.TokenType;
import dev.edugomes.springapi.dto.request.SignInRequest;
import dev.edugomes.springapi.dto.request.SignUpRequest;
import dev.edugomes.springapi.exception.UserAlreadyExistsException;
import dev.edugomes.springapi.jwt.JwtService;
import dev.edugomes.springapi.domain.Role;
import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.dto.response.AuthResponse;
import dev.edugomes.springapi.repository.TokenRepository;
import dev.edugomes.springapi.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;


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
    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
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

        saveUserToken(savedUser, accessToken);

        return AuthResponse.builder()
                .name(savedUser.getName())
                .email(signUpRequest.getEmail())
                .avatarUrl(savedUser.getAvatarUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }



    public AuthResponse authenticateUser(SignInRequest signInRequest) {

        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + signInRequest.getEmail()));

        // Check if the provided password matches the stored password
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
        saveUserToken(user, accessToken);

        return AuthResponse.builder()
                .name(user.getName())
                .email(signInRequest.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final String refreshToken = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userEmail));

        // If user and token is valid creates new authToken
        if(jwtService.isTokenValid(refreshToken, user)){
            String newAccessToken = jwtService.generateToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            revokeAllUserTokens(user);
            saveUserToken(user,newAccessToken);
            saveUserToken(user,newRefreshToken);

            var authResponse = AuthResponse.builder()
                    .name(user.getName())
                    .email(userEmail)
                    .avatarUrl(user.getAvatarUrl())
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();

            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);

        } else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

    }
}
