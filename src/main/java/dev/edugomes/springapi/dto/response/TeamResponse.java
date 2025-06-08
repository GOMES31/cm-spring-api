package dev.edugomes.springapi.dto.response;

import dev.edugomes.springapi.domain.Project;
import dev.edugomes.springapi.domain.TeamMember;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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

