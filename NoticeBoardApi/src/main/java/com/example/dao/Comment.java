package com.example.dao;

import com.example.dto.Recommendable;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Comment extends DateInfo implements Recommendable {
    @Id
    private String id;
    @DocumentReference
    private SiteUser author;
    @DocumentReference
    private Post post;
    private String content;
    private Map<String, List<String>> recommendUserIds;

    public Comment(SiteUser author, Post post, String content) {
        this.author = author;
        this.post = post;
        this.content = content;
        this.recommendUserIds = new HashMap<>(Map.of("recommend", new ArrayList<>(), "unRecommend", new ArrayList<>()));
    }
}
