package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyForm {
    private String authorId;
    private String commentId;
    private String replyContent;

    public ReplyForm(String authorId, String commentId, String replyContent) {
        this.authorId = authorId;
        this.commentId = commentId;
        this.replyContent = replyContent;
    }
}
