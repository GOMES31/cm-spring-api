package dev.edugomes.springapi.auth.response;

import dev.edugomes.springapi.user.Role;
import lombok.Data;

@Data
public class AuthResponse {

    private Long id;
    private String name;
    private String email;
    private String token;
    private Role role;
}
