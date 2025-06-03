package dev.edugomes.springapi.dto.request;

import lombok.Data;

@Data
public class UpdateTeamRequest {
    private String name;
    private String department;
    private String imageUrl;
}
