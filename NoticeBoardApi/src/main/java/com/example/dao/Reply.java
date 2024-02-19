package com.example.dao;

import com.example.dto.Recommendable;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "Reply")
@Getter
public class Reply extends DateInfo implements Recommendable {
    @Id
    private String id;
    @DocumentReference
    private SiteUser author;
    @DocumentReference
    private Comment comment;
    private String replyContent;
    private Map<String, List<String>> recommendUserIds;

    public Reply(SiteUser author, Comment comment, String replyContent) {
        this.author = author;
        this.comment = comment;
        this.replyContent = replyContent;
        this.recommendUserIds = new HashMap<>(Map.of("recommend", new ArrayList<>(), "unRecommend", new ArrayList<>()));
    }
}
