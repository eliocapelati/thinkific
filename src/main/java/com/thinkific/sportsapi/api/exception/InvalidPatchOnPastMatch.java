package com.thinkific.sportsapi.api.exception;

public class InvalidPatchOnPastMatch extends RuntimeException {
    private static final String MESSAGE = "It is not possible to update a match that is in the past";

    public InvalidPatchOnPastMatch() {
        super(MESSAGE);
    }
}
