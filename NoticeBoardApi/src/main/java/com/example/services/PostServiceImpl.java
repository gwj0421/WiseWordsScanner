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
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    public static final String UID_KEY = "Uid";
    private static final TargetType POST_TARGET_TYPE = TargetType.POST;
    private final WebClient.Builder loadBalancedWebClientBuilder;
    private final PostRepository postRepository;
    private final RecommendService recommendService;
    private final FormConverter formConverter;
    private final DeleteService deleteService;

    @Override
    public Mono<Post> createPost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Mono<PostForm> createPost(PostForm postForm, ServerHttpRequest request) {
        if (!request.getCookies().get(UID_KEY).isEmpty()) {
            String authorId = request.getCookies().get(UID_KEY).get(0).getValue();
            return formConverter.toPost(postForm, authorId)
                    .flatMap(postRepository::save)
                    .map(PostForm::getPostFormToShowDetail);
        }
        return Mono.empty();
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
    public Mono<String> findAuthorUserIdByAuthorId(String authorId) {
        return loadBalancedWebClientBuilder.build().get().uri("http://user-manage-api/user/id/"+authorId)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Override
    public Mono<Void> recommendPost(String postId, String recommenderId, boolean userRecommend) {
        return recommendService.recommend((ReactiveMongoRepository) postRepository, POST_TARGET_TYPE, postId, recommenderId, userRecommend);
    }

    @Override
    public Mono<PageablePostsResponse> getPostsByPage(int page, int size) {
        Sort sort = Sort.by(Sort.Order.desc("createdDate"));
        return postRepository.count()
                .flatMap(totalElements -> postRepository.findAllBy(PageRequest.of(page, size, sort))
                        .map(PostForm::getPostFormToShowTable)
                        .collectList()
                        .map(postForms ->
                                new PageablePostsResponse(page, size, totalElements, (int) Math.ceil((double) totalElements / size), postForms)
                        ));
    }

    @Override
    public Mono<PageablePostsResponse> getPostsBySearch(String keyword,int page, int size) {
        Sort sort = Sort.by(Sort.Order.desc("createdDate"));
        return postRepository.countAllByTitleContainingIgnoreCaseOrAuthorUserIdIsOrContentContainingIgnoreCase(keyword,keyword,keyword)
                .flatMap(totalElements -> postRepository.findAllByTitleContainingIgnoreCaseOrAuthorUserIdIsOrContentContainingIgnoreCase(keyword, keyword,keyword,PageRequest.of(page, size, sort))
                        .map(PostForm::getPostFormToShowTable)
                        .collectList()
                        .map(postForms ->
                                new PageablePostsResponse(page, size, totalElements, (int) Math.ceil((double) totalElements / size), postForms)
                        ));
    }

    @Override
    public Mono<PostForm> getPostDetailById(String id) {
        return postRepository.findPostById(id)
                .map(PostForm::getPostFormToShowDetail);
    }

    @Override
    public Mono<Void> deletePostById(String id) {
        return deleteService.deletePost(id);
    }

}