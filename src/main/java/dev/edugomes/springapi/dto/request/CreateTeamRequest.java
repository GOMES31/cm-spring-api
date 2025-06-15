package dev.edugomes.springapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateTeamRequest {

    private String name;

    private String department;

    @JsonProperty("image_url")
    private String imageUrl;

}