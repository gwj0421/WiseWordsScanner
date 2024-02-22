package com.example.dto;

import com.example.dao.Post;
import lombok.Getter;

import java.time.Instant;

import static com.example.services.RecommendServiceImpl.RECOMMEND_KEY;
import static com.example.services.RecommendServiceImpl.UN_RECOMMEND_KEY;

@Getter
public class PostForm {
    private String authorId;
    private String title;
    private String content;
    private String postId;
    private Instant createdDate;
    private String authorName;
    private int recommendCnt;
    private int unRecommendCnt;

    private PostForm() {
    }

    private PostForm(String postId, String title, Instant createdDate, String authorName, int recommendCnt) {
        this.postId = postId;
        this.title = title;
        this.createdDate = createdDate;
        this.authorName = authorName;
        this.recommendCnt = recommendCnt;
    }

    public PostForm(String title, String content, Instant createdDate, String authorName, int recommendCnt, int unRecommendCnt) {
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.authorName = authorName;
        this.recommendCnt = recommendCnt;
        this.unRecommendCnt = unRecommendCnt;
    }

    public static PostForm getPostToShowTable(Post post) {
        return new PostForm(post.getId(), post.getTitle(), post.getCreatedDate(),post.getAuthor().getName(),post.getRecommendUserIds().get(RECOMMEND_KEY).size());
    }

    public static PostForm getPostToShowDetail(Post post) {
        return new PostForm(post.getTitle(), post.getContent(), post.getCreatedDate(), post.getAuthor().getName(), post.getRecommendUserIds().get(RECOMMEND_KEY).size(), post.getRecommendUserIds().get(UN_RECOMMEND_KEY).size());
    }

}
