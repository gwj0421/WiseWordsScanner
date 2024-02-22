package com.example.dto;

import lombok.Getter;

@Getter
public class Paging {
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public Paging(int currentPage, int pageSize, long totalElements, int totalPages) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
