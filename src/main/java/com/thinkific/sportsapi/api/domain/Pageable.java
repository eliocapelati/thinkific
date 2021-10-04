package com.thinkific.sportsapi.api.domain;

public record Pageable(Integer page, Integer size) {
    public Pageable(){
        this(0, 100);
    }
}
