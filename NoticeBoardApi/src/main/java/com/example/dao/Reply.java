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

@Document(collection = "reply")
@Getter
public class Reply extends DateInfo implements Recommendable {
    @Id
    private String id;
    @DocumentReference
    private Comment comment;
    private String authorId;
    private String authorUserId;
    private String replyContent;
    private Map<String, List<String>> recommendUserIds;

    public Reply(Comment comment,String authorId, String authorUserId, String replyContent) {
        this.comment = comment;
        this.authorId = authorId;
        this.authorUserId = authorUserId;
        this.replyContent = replyContent;
        this.recommendUserIds = new HashMap<>(Map.of("recommend", new ArrayList<>(), "unRecommend", new ArrayList<>()));
    }
}
