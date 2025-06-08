package dev.edugomes.springapi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.edugomes.springapi.domain.Status;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class ProjectResponse {

    private Long id;

    private String name;

    private String description;

    @JsonProperty("start_date")
    private Date startDate;

    @JsonProperty("end_date")
    private Date endDate;

    private Status status;

    private TeamInfo team;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;

    private List<TaskInfo> tasks;

    @Data
    @Builder
    public static class TeamInfo {
        private Long id;
        private String name;
        private String department;
    }

    @Data
    @Builder
    public static class TaskInfo {
        private Long id;
        private String title;
        private String description;
        private Status status;

        @JsonProperty("start_date")
        private Date startDate;

        @JsonProperty("end_date")
        private Date endDate;
    }
}
