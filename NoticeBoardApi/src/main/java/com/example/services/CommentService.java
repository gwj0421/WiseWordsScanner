package com.example.services;

import com.example.dao.Comment;
import com.example.dto.CommentForm;
import com.example.dto.CommentWithReplies;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CommentService {
    Mono<Comment> createComment(Comment comment);

    Mono<String> createComment(ServerHttpRequest request, CommentForm commentForm);

    Mono<Comment> readCommentById(String id);

    Flux<Comment> readCommentsByAuthorId(String id);

    Mono<List<CommentWithReplies>> readCommentsByPostId(String postId, String authorId);

    Mono<Void> deleteCommentById(String id);
}
