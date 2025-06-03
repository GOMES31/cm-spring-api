package dev.edugomes.springapi.dto.request;

import lombok.Data;

@Data
public class AddTeamMemberRequest {
    private Long userId;
    private String role;
}
