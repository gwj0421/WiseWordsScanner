package com.example.dao;

import com.example.dto.TargetType;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "recommendations")
@Getter
public class Recommendation {
    @Id
    private String id;
    private String userId;
    private TargetType targetType;
    private String targetId;
    private boolean isRecommend;

    public Recommendation(String userId, TargetType targetType, String targetId, boolean isRecommend) {
        this.userId = userId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.isRecommend = isRecommend;
    }

    public void switchRecommend() {
        this.isRecommend = !this.isRecommend;
    }
}
