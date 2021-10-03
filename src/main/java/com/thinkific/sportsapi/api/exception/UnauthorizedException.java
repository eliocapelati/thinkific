package com.thinkific.sportsapi.api.exception;

public class UnauthorizedException extends RuntimeException {
    private static final String message = "Unauthorized";

    public UnauthorizedException() {
        super(message);
    }
}
