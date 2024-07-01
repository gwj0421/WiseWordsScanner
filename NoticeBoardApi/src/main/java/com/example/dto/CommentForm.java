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
    private int recommendCnt;
    private boolean isRecommend;

    @JsonCreator
    public CommentForm(String postId, String content) {
        this.postId = postId;
        this.content = content;
    }

    private CommentForm(String authorUserId, String content,int recommendCnt,boolean isRecommend) {
        this.authorUserId = authorUserId;
//        this.commentId = commentId;
        this.content = content;
        this.recommendCnt = recommendCnt;
        this.isRecommend = isRecommend;
    }

    private CommentForm(String authorUserId, String commentId, String content,int recommendCnt,boolean isRecommend) {
        this.authorUserId = authorUserId;
        this.commentId = commentId;
        this.content = content;
        this.recommendCnt = recommendCnt;
        this.isRecommend = isRecommend;
    }

    public static CommentForm getCommentFormToShowDetail(Comment comment,String authorId) {
        return new CommentForm(comment.getAuthorUserId(), comment.getId(), comment.getContent(), comment.getRecommendUserIds().size(),comment.getRecommendUserIds().contains(authorId));
    }
}
