package com.thinkific.sportsapi.api.exception;

public class AlreadyExistsException extends RuntimeException {
    private static final String message = "Theres a user with this email";
    private static final String messageGeneric = "Theres a %s with this %s";

    public AlreadyExistsException() {
        super(message);
    }

    public AlreadyExistsException(String resource, String value) {
        super(messageGeneric.formatted(resource, value));
    }
}
