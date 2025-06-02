package dev.edugomes.springapi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class TeamMemberResponse {

    private Long id;

    private String name;

    private String email;

    private String role;
}

