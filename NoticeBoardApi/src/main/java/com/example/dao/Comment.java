package com.example.dao;

import com.example.dto.DateInfo;
import com.example.dto.Recommendable;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "comment")
@Getter
public class Comment extends DateInfo implements Recommendable {
    @Id
    private String id;
    @DocumentReference
    private Post post;
    private String authorId;
    private String authorUserId;
    private String content;
    private Map<String, List<String>> recommendUserIds;

    public Comment(Post post,String authorId,String authorUserId, String content) {
        this.post = post;
        this.authorId = authorId;
        this.authorUserId = authorUserId;
        this.content = content;
        this.recommendUserIds = new HashMap<>(Map.of("recommend", new ArrayList<>(), "unRecommend", new ArrayList<>()));
    }
}
