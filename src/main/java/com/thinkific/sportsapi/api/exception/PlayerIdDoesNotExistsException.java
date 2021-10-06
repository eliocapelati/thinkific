package com.thinkific.sportsapi.api.exception;

import java.util.List;

public class PlayerIdDoesNotExistsException extends RuntimeException {
    private List<String> notExists;

    public PlayerIdDoesNotExistsException(List<String> notExists) {
        this.notExists = notExists;
    }

    public List<String> getNotExists() {
        return notExists;
    }
}
