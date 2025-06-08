package dev.edugomes.springapi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.edugomes.springapi.domain.Status;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Status status;

    @JsonProperty("start_date")
    private Date startDate;

    @JsonProperty("end_date")
    private Date endDate;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;

    private ProjectInfo project;
    private List<TeamMemberInfo> assignees;

    @Data
    @Builder
    public static class ProjectInfo {
        private Long id;
        private String name;
        private String description;
        private Status status;
    }

    @Data
    @Builder
    public static class TeamMemberInfo {
        private Long id;
        private String name;
        private String email;

        @JsonProperty("team_role")
        private String teamRole;

        @JsonProperty("avatar_url")
        private String avatarUrl;
    }

    @Data
    @Builder
    public static class ObservationInfo {
        private Long id;
        private String message;
        @JsonProperty("created_at")
        private Date createdAt;
    }
}