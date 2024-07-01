package com.example.dao;

import com.example.dto.DateInfo;
import com.example.dto.Recommendable;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    private Set<String> recommendUserIds;

    public Reply(Comment comment, String authorId, String authorUserId, String replyContent) {
        this.comment = comment;
        this.authorId = authorId;
        this.authorUserId = authorUserId;
        this.replyContent = replyContent;
        this.recommendUserIds = new HashSet<>();
    }

    @Override
    public Set<String> getNotRecommendUserIds() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getRecommendUserIds() {
        return recommendUserIds;
    }
}
