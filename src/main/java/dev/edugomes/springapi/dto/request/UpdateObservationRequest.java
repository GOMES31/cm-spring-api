package dev.edugomes.springapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class UpdateObservationRequest {

    @NotNull(message = "Observation ID is required for update")
    private Long id;

    @NotBlank(message = "Message is required")
    private String message;

    @JsonProperty("image_url")
    private String imageUrl;
}