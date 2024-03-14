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
    private String authorId;
    private String authorUserId;
    private String title;
    private String content;
    private long visitCnt;
    private Map<String, List<String>> recommendUserIds;

    public Post(String authorId, String authorUserId,String title, String content) {
        this.authorId = authorId;
        this.authorUserId = authorUserId;
        this.title = title;
        this.content = content;
        this.visitCnt = 0;
        this.recommendUserIds = new HashMap<>(Map.of("recommend", new ArrayList<>(), "unRecommend", new ArrayList<>()));
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void visitPost() {
        this.visitCnt++;
    }
}
