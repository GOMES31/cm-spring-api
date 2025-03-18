package dev.edugomes.springapi.util;

import dev.edugomes.springapi.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ResponseHandler {

    public static <T> ResponseEntity<ApiResponse<T>> buildResponse(String message, HttpStatus status, T data) {
        ApiResponse<T> response = new ApiResponse<>(status.value(), message, data);
        return new ResponseEntity<>(response, status);
    }
}
