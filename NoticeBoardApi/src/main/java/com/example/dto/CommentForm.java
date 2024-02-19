package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
    private String authorId;
    private String postId;
    private String content;

    public CommentForm(String authorId, String postId, String content) {
        this.authorId = authorId;
        this.postId = postId;
        this.content = content;
    }
}
