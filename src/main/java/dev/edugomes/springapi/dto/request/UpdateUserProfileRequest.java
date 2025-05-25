package dev.edugomes.springapi.dto.request;

import lombok.Data;

@Data
public class UpdateUserProfileRequest {
    private String name;
    private String password;
    private String avatarUrl;
}