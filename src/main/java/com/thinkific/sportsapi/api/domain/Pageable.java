package com.thinkific.sportsapi.api.domain;

public class Pageable {
    private Integer page = 0;
    private Integer size = 100;

    public Pageable() {
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Pageable{" +
                "page=" + page +
                ", size=" + size +
                '}';
    }
}
