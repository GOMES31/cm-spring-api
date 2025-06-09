package dev.edugomes.springapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.edugomes.springapi.domain.Status;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

@Data
public class UpdateTaskRequest {

    @NotNull(message = "Task ID is required for update")
    private Long id;

    private String title;

    private String description;

    private Status status;

    @JsonProperty("start_date")
    private Date startDate;

    @JsonProperty("end_date")
    private Date endDate;

    @JsonProperty("assignee_ids")
    private List<Long> assigneeIds;
}