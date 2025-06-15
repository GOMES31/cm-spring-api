package dev.edugomes.springapi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TeamResponse {

    private Long id;
    private String name;

    private String department;

    @JsonProperty("image_url")
    private String imageUrl;

    private List<TeamMemberResponse> members;
}

