package com.thinkific.sportsapi.api.exception;

public class NoContentException extends RuntimeException {
    static final String messageGeneric = "No content for %s";

    public NoContentException(String resource) {
        super(messageGeneric.formatted(resource));
    }
}
