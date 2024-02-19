package com.example.dto;

public enum TargetType {
    SITE_USER("siteUser"),
    POST("post"),
    COMMENT("comment"),
    REPLY("reply");

    private String desc;

    TargetType(String desc) {
        this.desc = desc;
    }
}
