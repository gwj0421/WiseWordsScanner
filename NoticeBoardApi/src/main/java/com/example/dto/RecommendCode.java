package com.example.dto;

import lombok.Getter;

@Getter
public enum RecommendCode {
    RECOMMEND(1),
    NOT_RECOMMEND(-1),
    NONE(0);

    private int code;

    RecommendCode(int code) {
        this.code = code;
    }

    public static RecommendCode getRecommendCode(Recommendable entity, String authorId) {
        if (entity.getRecommendUserIds().contains(authorId)) {
            return RecommendCode.RECOMMEND;
        } else if (entity.getNotRecommendUserIds().contains(authorId)) {
            return RecommendCode.NOT_RECOMMEND;
        } else {
            return RecommendCode.NONE;
        }

    }
}
