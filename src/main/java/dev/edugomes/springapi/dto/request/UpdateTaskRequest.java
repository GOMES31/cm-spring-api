package dev.edugomes.springapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.edugomes.springapi.domain.Status;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UpdateTaskRequest {

    private String title;

    private String description;

    private Status status;

    @JsonProperty("end_date")
    private Date endDate;

    @JsonProperty("assignee_ids")
    private List<Long> assigneeIds;
}