package com.example.config;

public enum RecommendType {
    RECOMMEND("recommend"),
    NOT_RECOMMEND("notRecommend");

    RecommendType(String recommendType) {
        this.recommendType = recommendType;
    }

    private String recommendType;

    public String getRecommendType() {
        return recommendType;
    }
}
