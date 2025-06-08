package dev.edugomes.springapi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserProfileResponse {
    private String name;

    private String email;

    @JsonProperty("avatar_url")
    private String avatarUrl;
}