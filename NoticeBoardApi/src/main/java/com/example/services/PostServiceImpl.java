package com.example.services;

import com.example.dao.Post;
import com.example.dto.PageablePostsResponse;
import com.example.dto.PostForm;
import com.example.dto.TargetType;
import com.example.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
    public Mono<PageablePostsResponse> getPostsByPage(int page, int size) {
        Sort sort = Sort.by(Sort.Order.desc("createdDate"));
        return postRepository.findAllBy(PageRequest.of(page,size,sort))
                .map(PostForm::getPostToShowTable)
                .collectList()
                .zipWith(postRepository.count())
                .map(tuple -> {
                    List<PostForm> posts = tuple.getT1();
                    Long totalElements = tuple.getT2();
                    int totalPages = (int) Math.ceil((double) totalElements / size);
                    return new PageablePostsResponse(page,size,totalElements,totalPages,posts);
                });
    }

    @Override
    public Mono<PostForm> getPostDetailById(String id) {
        return postRepository.findPostById(id)
                .map(PostForm::getPostToShowDetail);
    }

    @Override
    public Mono<Void> deletePostById(String id) {
        return deleteService.deletePost(id);
    }

}