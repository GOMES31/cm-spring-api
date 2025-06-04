package dev.edugomes.springapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CreateObservationRequest {

    @NotNull(message = "Task ID is required")
    @JsonProperty("task_id")
    private Long taskId;

    @NotBlank(message = "Message is required")
    private String message;

    @JsonProperty("image_url")
    private String imageUrl;
}
