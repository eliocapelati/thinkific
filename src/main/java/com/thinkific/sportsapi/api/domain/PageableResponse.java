package com.thinkific.sportsapi.api.domain;

import java.util.List;

public class PageableResponse<T> {
    private List<T> content;
    private int totalPages;
    private int totalElements;
    private int size;

    public PageableResponse(List<T> content, int totalPages, int totalElements) {
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

    public int getTotalElements() {
        return totalElements;
    }

    public int getSize() {
        return size;
    }
}
