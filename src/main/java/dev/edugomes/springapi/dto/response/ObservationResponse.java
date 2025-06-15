package dev.edugomes.springapi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ObservationResponse {

    private Long id;
    private String message;

    @JsonProperty("created_at")
    private Date createdAt;

    private TaskInfo task;
    private UserInfo user;
    private String imageUrl;

    @Data
    @Builder
    public static class TaskInfo {
        private Long id;
        private String title;
    }

    @Data
    @Builder
    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
    }

}