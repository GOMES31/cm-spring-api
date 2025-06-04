package dev.edugomes.springapi.exception;

public class TeamMemberNotFoundException extends RuntimeException {
    public TeamMemberNotFoundException(String message) {
        super(message);
    }
}
