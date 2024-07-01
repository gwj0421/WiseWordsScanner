package com.example.dto;

import lombok.Getter;

@Getter
public enum TargetType {
    POST("post",true),
    COMMENT("comment",false),
    REPLY("reply",false),
    NONE("none",false);

    private String desc;
    private boolean withNotRecommend;

    TargetType(String desc, boolean withNotRecommend) {
        this.desc = desc;
        this.withNotRecommend = withNotRecommend;
    }

    public static TargetType findTargetType(String targetName) {
        for (TargetType targetType : values()) {
            if (targetType.getDesc().equals(targetName)) {
                return targetType;
            }
        }
        return TargetType.NONE;
    }
}
