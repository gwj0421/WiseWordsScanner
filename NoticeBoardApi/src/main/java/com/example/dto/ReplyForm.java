package com.example.dto;

import com.example.dao.Reply;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReplyForm {
    private String authorUserId;
    private String commentId;
    private String replyId;
    private String replyContent;

    @JsonCreator
    public ReplyForm(String commentId, String replyContent) {
        this.commentId = commentId;
        this.replyContent = replyContent;
    }

    private ReplyForm(String authorUserId, String replyId, String replyContent) {
        this.authorUserId = authorUserId;
        this.replyId = replyId;
        this.replyContent = replyContent;
    }

    public static ReplyForm getReplyFormToShowDetail(Reply reply) {
        return new ReplyForm(reply.getAuthorUserId(), reply.getId(), reply.getReplyContent());
    }
}
