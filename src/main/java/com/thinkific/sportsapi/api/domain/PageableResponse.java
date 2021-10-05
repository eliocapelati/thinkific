package com.thinkific.sportsapi.api.domain;

import java.util.List;

public class PageableResponse<T> {
    private final List<T> content;
    private final int totalPages;
    private final long totalElements;
    private final int size;

    public PageableResponse(List<T> content, int totalPages, long totalElements) {
        this.content = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = content.size();
    }

    public List<T> getContent() {
        return content;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getSize() {
        return size;
    }
}
