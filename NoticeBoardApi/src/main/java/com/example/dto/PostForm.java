package com.example.dto;

import com.example.dao.Post;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
public class PostForm {
    private String postId;
    private String authorUserId;
    private String title;
    private String content;
    private String createdDate;
    private int recommendCnt;
    private int notRecommendCnt;
    private int recommendCode;
    private long visitCnt;

    @JsonCreator
    public PostForm(String title, String content) {
        this.title = title;
        this.content = content;
    }

    private PostForm(String postId, String title, Instant createdDate, String authorUserId, int recommendCnt, long visitCnt) {
        this.postId = postId;
        this.title = title;
        this.createdDate = convertTime(createdDate);
        this.authorUserId = authorUserId;
        this.recommendCnt = recommendCnt;
        this.visitCnt = visitCnt;
    }

    private PostForm(String postId, String title, String content, Instant createdDate, String authorUserId, int recommendCnt, int notRecommendCnt, long visitCnt, int recommendCode) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.createdDate = convertTime(createdDate);
        this.authorUserId = authorUserId;
        this.recommendCnt = recommendCnt;
        this.notRecommendCnt = notRecommendCnt;
        this.visitCnt = visitCnt;
        this.recommendCode = recommendCode;
    }

    public static PostForm getPostFormToShowTable(Post post) {
        return new PostForm(post.getId(), post.getTitle(), post.getCreatedDate(), post.getAuthorUserId(), post.getRecommendUserIds().size(), post.getVisitCnt());
    }

    public static PostForm getPostFormToShowDetail(Post post, String authorId) {
        return new PostForm(post.getId(), post.getTitle(), post.getContent(), post.getCreatedDate(), post.getAuthorUserId(),
                post.getRecommendUserIds().size(), post.getNotRecommendUserIds().size(), post.getVisitCnt(), RecommendCode.getRecommendCode(post,authorId).getCode());
    }

    private static String convertTime(Instant time) {
        return LocalDateTime.ofInstant(time, ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yy-MM-dd"));
    }

}
