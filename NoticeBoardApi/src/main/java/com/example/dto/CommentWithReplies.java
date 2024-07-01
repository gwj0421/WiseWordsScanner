package com.example.dto;

import com.example.dao.Comment;
import lombok.Getter;

import java.util.List;

@Getter
public class CommentWithReplies {
    private CommentForm comment;
    private List<ReplyForm> replies;

    public CommentWithReplies(Comment comment, List<ReplyForm> replies, String authorId) {
        this.comment = CommentForm.getCommentFormToShowDetail(comment,authorId);
        this.replies = replies;
    }
}
