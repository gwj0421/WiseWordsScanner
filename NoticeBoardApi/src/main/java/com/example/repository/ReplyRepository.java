package com.example.repository;

import com.example.dao.Reply;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReplyRepository extends ReactiveMongoRepository<Reply, String> {
    Flux<Reply> findRepliesByCommentId(String id);

    Mono<Void> deleteRepliesByAuthorId(String authorId);

    Mono<Void> deleteRepliesByCommentId(String commentId);

    Mono<Void> deleteRepliesByCommentPostId(String postId);

}
