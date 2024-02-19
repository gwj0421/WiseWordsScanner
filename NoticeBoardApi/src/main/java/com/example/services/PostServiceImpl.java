package com.example.services;

import com.example.dao.Post;
import com.example.dto.PostForm;
import com.example.dto.TargetType;
import com.example.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private static final TargetType POST_TARGET_TYPE = TargetType.POST;
    private final PostRepository postRepository;
    private final RecommendService recommendService;
    private final FormConverter formConverter;
    private final DeleteService deleteService;

    @Override
    public Mono<Post> createPost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Mono<Post> createPost(PostForm postForm) {
        return formConverter.toPost(postForm)
                .flatMap(postRepository::save);
    }

    @Override
    public Mono<Post> readPostById(String id) {
        return postRepository.findPostById(id).switchIfEmpty(Mono.empty());
    }

    @Override
    public Flux<Post> readPostsByAuthorId(String id) {
        return postRepository.findPostsByAuthorId(id);
    }

    @Override
    public Mono<Void> recommendPost(String postId, String recommenderId,boolean userRecommend) {
        return recommendService.recommend((ReactiveMongoRepository) postRepository, POST_TARGET_TYPE,postId,recommenderId,userRecommend);
    }

    @Override
    public Mono<Void> deletePostById(String id) {
        return deleteService.deletePost(id);
    }

}