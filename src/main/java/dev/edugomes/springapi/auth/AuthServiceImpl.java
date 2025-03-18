package dev.edugomes.springapi.auth;

import dev.edugomes.springapi.auth.request.LoginRequest;
import dev.edugomes.springapi.auth.request.RegisterRequest;
import dev.edugomes.springapi.jwt.JwtService;
import dev.edugomes.springapi.user.Role;
import dev.edugomes.springapi.user.User;
import dev.edugomes.springapi.auth.response.AuthResponse;
import dev.edugomes.springapi.user.UserMapper;
import dev.edugomes.springapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse registerUser(RegisterRequest registerRequest) {
        User user = userMapper.toEntity(registerRequest);

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        var jwtToken = jwtService.generateToken(savedUser);

        AuthResponse authResponse = userMapper.toDto(savedUser);
        authResponse.setToken(jwtToken);

        return authResponse;
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();

        var jwtToken = jwtService.generateToken(user);

        AuthResponse authResponse = userMapper.toDto(user);
        authResponse.setToken(jwtToken);

        return authResponse;
    }
}
