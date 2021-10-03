package com.thinkific.sportsapi.api.exception;

public class CantSignupUserException extends RuntimeException {
    private static final String message = "Cant signup user, try again later";

    public CantSignupUserException() {
        super(message);
    }
}
