package com.example.services;

import com.example.dao.Comment;
import com.example.dto.CommentForm;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentService {
    Mono<Comment> createComment(Comment comment);
    Mono<Comment> createComment(CommentForm commentForm);
    Mono<Comment> readCommentById(String id);
    Flux<Comment> readCommentsByAuthorId(String id);
    Flux<Comment> readCommentsByPostId(String id);

    Mono<Void> recommend(String commentId, String recommenderId, boolean userRecommend);
    Mono<Void> deleteCommentById(String id);
}
