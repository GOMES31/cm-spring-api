// src/main/java/dev/edugomes/springapi/controller/UserController.java
package dev.edugomes.springapi.controller;

import dev.edugomes.springapi.common.ApiResponse;
import dev.edugomes.springapi.dto.request.UpdateUserProfileRequest;
import dev.edugomes.springapi.dto.response.UpdateUserProfileResponse;
import dev.edugomes.springapi.exception.UserNotFoundException;
import dev.edugomes.springapi.service.user.UserService;
import dev.edugomes.springapi.util.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private static final String UPDATE_USER_PROFILE = "/update-profile";

    @PutMapping(
            value = UPDATE_USER_PROFILE,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<UpdateUserProfileResponse>> updateUserProfile(@Valid @RequestBody UpdateUserProfileRequest request) {
        try {
            UpdateUserProfileResponse response = userService.updateProfile(request);
            return ResponseHandler.buildResponse("Profile updated successfully", HttpStatus.OK, response);
        } catch (UserNotFoundException e) {
            return ResponseHandler.buildResponse("User not found", HttpStatus.NOT_FOUND, null);
        } catch (Exception e) {
            return ResponseHandler.buildResponse("Could not update profile", HttpStatus.BAD_REQUEST, null);
        }
    }
}
