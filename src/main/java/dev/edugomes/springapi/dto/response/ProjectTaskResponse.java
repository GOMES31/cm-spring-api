package dev.edugomes.springapi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.edugomes.springapi.domain.TeamMember;
import lombok.Builder;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class ProjectTaskResponse {

    private Long id;

    private String title;

    private String description;

    private String status;

    @JsonProperty("start_date")
    private Date startDate;

    @JsonProperty("end_date")
    private Date endDate;

    private List<TeamMember> assignees;
}

