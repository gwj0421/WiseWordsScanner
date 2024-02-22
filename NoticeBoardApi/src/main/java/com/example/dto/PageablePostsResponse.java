package com.example.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PageablePostsResponse extends Paging {
    private List<PostForm> posts;

    public PageablePostsResponse(int currentPage, int pageSize, long totalElements, int totalPages, List<PostForm> posts) {
        super(currentPage, pageSize, totalElements, totalPages);
        this.posts = posts;
    }
}
