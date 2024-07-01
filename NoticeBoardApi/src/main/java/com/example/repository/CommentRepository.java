package com.example.repository;

import com.example.dao.Comment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentRepository extends ReactiveMongoRepository<Comment, String> {
    Flux<Comment> findCommentsByAuthorId(String id);
    Flux<Comment> findCommentsByPostIdOrderByCreatedDate(String id);
    Mono<Void> deleteCommentsByAuthorId(String authorId);
    Mono<Void> deleteCommentsByPostId(String postId);
}
