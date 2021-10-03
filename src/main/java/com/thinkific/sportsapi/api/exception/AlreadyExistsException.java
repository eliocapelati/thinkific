package com.thinkific.sportsapi.api.exception;

public class AlreadyExistsException extends RuntimeException {
    private static final String message = "Theres a user with this email";
    public AlreadyExistsException() {
        super(message);
    }
}
