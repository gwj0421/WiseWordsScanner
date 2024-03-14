package com.example.dto;

import com.example.dao.Post;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.time.Instant;

import static com.example.services.RecommendServiceImpl.RECOMMEND_KEY;
import static com.example.services.RecommendServiceImpl.UN_RECOMMEND_KEY;

@Getter
public class PostForm {
    private String postId;
    private String authorUserId;
    private String title;
    private String content;
    private Instant createdDate;
    private int recommendCnt;
    private int unRecommendCnt;
    private long visitCnt;

    @JsonCreator
    public PostForm(String title, String content) {
        this.title = title;
        this.content = content;
    }

    private PostForm(String postId, String title, Instant createdDate, String authorUserId, int recommendCnt,long visitCnt) {
        this.postId = postId;
        this.title = title;
        this.createdDate = createdDate;
        this.authorUserId = authorUserId;
        this.recommendCnt = recommendCnt;
        this.visitCnt = visitCnt;
    }

    private PostForm(String postId, String title, String content, Instant createdDate, String authorUserId, int recommendCnt, int unRecommendCnt, long visitCnt) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.authorUserId = authorUserId;
        this.recommendCnt = recommendCnt;
        this.unRecommendCnt = unRecommendCnt;
        this.visitCnt = visitCnt;
    }

    public static PostForm getPostFormToShowTable(Post post) {
        return new PostForm(post.getId(), post.getTitle(), post.getCreatedDate(), post.getAuthorUserId(), post.getRecommendUserIds().get(RECOMMEND_KEY).size(), post.getVisitCnt());
    }

    public static PostForm getPostFormToShowDetail(Post post) {
        return new PostForm(post.getId(), post.getTitle(), post.getContent(), post.getCreatedDate(), post.getAuthorUserId(), post.getRecommendUserIds().get(RECOMMEND_KEY).size(), post.getRecommendUserIds().get(UN_RECOMMEND_KEY).size(), post.getVisitCnt());
    }
}
