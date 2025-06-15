package dev.edugomes.springapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {

    private String name;

    private String password;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("updated_at")
    private Long updatedAt;
}