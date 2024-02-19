package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostForm {
    private String authorId;
    private String content;

    public PostForm(String authorId, String content) {
        this.authorId = authorId;
        this.content = content;
    }
}
