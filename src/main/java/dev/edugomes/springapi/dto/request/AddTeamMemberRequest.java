package dev.edugomes.springapi.dto.request;

import lombok.Data;

@Data
public class AddTeamMemberRequest {
    private String email;
    private String role;
}
