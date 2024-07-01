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
    private Set<String> recommend;

    public Comment(Post post, String authorId, String authorUserId, String content) {
        this.post = post;
        this.authorId = authorId;
        this.authorUserId = authorUserId;
        this.content = content;
        this.recommend = new HashSet<>();
    }

    @Override
    public Set<String> getNotRecommendUserIds() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getRecommendUserIds() {
        return recommend;
    }
}
