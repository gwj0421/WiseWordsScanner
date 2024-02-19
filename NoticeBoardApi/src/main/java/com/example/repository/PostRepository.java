package com.example.repository;

import com.example.dao.Post;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepository extends ReactiveMongoRepository<Post, String> {
    Mono<Post> findPostById(String id);
    Flux<Post> findPostsByAuthorId(String id);
    Flux<Post> findByAuthorId(String authorId);

    Mono<Void> deletePostsByAuthorId(String authorId);
}
