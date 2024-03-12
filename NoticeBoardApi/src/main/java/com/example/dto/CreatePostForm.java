package com.example.dto;

import lombok.Getter;

@Getter
public class CreatePostForm {
    private String title;
    private String content;

    public CreatePostForm(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
