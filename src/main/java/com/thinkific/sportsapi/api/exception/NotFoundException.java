package com.thinkific.sportsapi.api.exception;

public class NotFoundException extends RuntimeException {
    static final String messageGeneric = "%s cant be found";

    public NotFoundException(String resource) {
        super(messageGeneric.formatted(resource));
    }
}
