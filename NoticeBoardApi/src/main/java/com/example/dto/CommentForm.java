package com.example.dto;

import com.example.dao.Comment;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentForm {
    private String authorUserId;
    private String postId;
    private String commentId;
    private String content;

    @JsonCreator
    public CommentForm(String postId, String content) {
        this.postId = postId;
        this.content = content;
    }

    private CommentForm(String authorUserId, String commentId, String content) {
        this.authorUserId = authorUserId;
        this.commentId = commentId;
        this.content = content;
    }

    public static CommentForm getCommentFormToShowDetail(Comment comment) {
        return new CommentForm(comment.getAuthorUserId(), comment.getId(), comment.getContent());
    }
}
