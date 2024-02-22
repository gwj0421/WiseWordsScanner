package com.example.dao;

import com.example.dto.DateInfo;
import com.example.dto.Recommendable;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "post")
@Getter
public class Post extends DateInfo implements Recommendable {
    @Id
    private String id;
    private SiteUser author;
    private String title;
    private String content;
    private Map<String, List<String>> recommendUserIds;

    public Post(SiteUser author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.recommendUserIds = new HashMap<>(Map.of("recommend", new ArrayList<>(), "unRecommend", new ArrayList<>()));
    }

    public void changeContent(String content) {
        this.content = content;
    }
}
