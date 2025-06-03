package dev.edugomes.springapi.exception;

public class ObservationNotFoundException extends RuntimeException {
    public ObservationNotFoundException(String message) {
        super(message);
    }
}