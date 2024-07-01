package com.example.dao;

import com.example.config.RecommendType;
import com.example.dto.DateInfo;
import com.example.dto.Recommendable;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Document(collection = "post")
@Getter
public class Post extends DateInfo implements Recommendable {
    @Id
    private String id;
    private String authorId;
    private String authorUserId;
    private String title;
    private String content;
    private long visitCnt;
    private Map<RecommendType, Set<String>> recommendWithNotRecommendUserIds;

    public Post(String authorId, String authorUserId,String title, String content) {
        this.authorId = authorId;
        this.authorUserId = authorUserId;
        this.title = title;
        this.content = content;
        this.visitCnt = 0;
        this.recommendWithNotRecommendUserIds = new HashMap<>(Map.of(RecommendType.RECOMMEND, new HashSet<>(), RecommendType.NOT_RECOMMEND, new HashSet<>()));
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void visitPost() {
        this.visitCnt++;
    }

    @Override
    public Set<String> getNotRecommendUserIds() {
        return recommendWithNotRecommendUserIds.get(RecommendType.NOT_RECOMMEND);
    }

    @Override
    public Set<String> getRecommendUserIds() {
        return recommendWithNotRecommendUserIds.get(RecommendType.RECOMMEND);
    }
}
