package dev.edugomes.springapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateTeamMemberRequest {

    private String role;

    @JsonProperty("updated_at")
    private Long updatedAt;

}
