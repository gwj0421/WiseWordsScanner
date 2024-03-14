package com.example.repository;

import com.example.dao.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepository extends ReactiveMongoRepository<Post, String> {
    Mono<Post> findPostById(String id);
    Flux<Post> findPostsByAuthorId(String id);
    Flux<Post> findByAuthorId(String authorId);
    Flux<Post> findAllBy(Pageable pageable);

    Mono<Long> countAllByTitleContainingIgnoreCaseOrAuthorUserIdIsOrContentContainingIgnoreCase(String titleKeyword, String userIdKeyword, String contentKeyword);
    Flux<Post> findAllByTitleContainingIgnoreCaseOrAuthorUserIdIsOrContentContainingIgnoreCase(String titleKeyword, String userIdKeyword,String contentKeyword,Pageable pageable);
    Flux<Post> findAllByTitleContainingIgnoreCaseOrAuthorUserIdIsOrContentContainingIgnoreCase(String titleKeyword, String userIdKeyword,String contentKeyword);
    Mono<Void> deletePostsByAuthorId(String authorId);
}
