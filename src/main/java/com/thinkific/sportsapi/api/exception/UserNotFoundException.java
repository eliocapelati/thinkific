package com.thinkific.sportsapi.api.exception;

public class UserNotFoundException extends RuntimeException {
    static final String message = "User cant be found locally";
    public UserNotFoundException() {
        super(message);
    }
}
