package com.example.services;

import com.example.dao.Post;
import com.example.dto.PageablePostsResponse;
import com.example.dto.PostForm;
import com.example.dto.PostPageForm;
import com.example.exception.error.EmptyPostIdException;
import com.example.repository.PostRepository;
import com.example.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {
    private final CommentService commentService;
    private final WebClient.Builder loadBalancedWebClientBuilder;
    private final PostRepository postRepository;
    private final FormConverter formConverter;
    private final DeleteService deleteService;
    private final ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    @Override
    public Mono<String> createPost(PostForm postForm, ServerHttpRequest request) {
        String authorId = CookieUtils.getUserId(request);
        return formConverter.toPost(postForm, authorId)
                .flatMap(postRepository::save)
                .map(Post::getId);
    }

    @Override
    public Mono<Post> readPostById(String id) {
        return postRepository.findPostById(id).switchIfEmpty(Mono.defer(()-> Mono.error(new EmptyPostIdException("readPostById"))));
    }

    @Override
    public Flux<Post> readPostsByAuthorId(String id) {
        return postRepository.findPostsByAuthorId(id);
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
    public Mono<PageablePostsResponse> getPostsBySearch(String keyword, int page, int size) {
        Sort sort = Sort.by(Sort.Order.desc("createdDate"));
        return postRepository.countAllByTitleContainingIgnoreCaseOrAuthorUserIdIsOrContentContainingIgnoreCase(keyword, keyword, keyword)
                .flatMap(totalElements -> postRepository.findAllByTitleContainingIgnoreCaseOrAuthorUserIdIsOrContentContainingIgnoreCase(keyword, keyword, keyword, PageRequest.of(page, size, sort))
                        .map(PostForm::getPostFormToShowTable)
                        .collectList()
                        .map(postForms ->
                                new PageablePostsResponse(page, size, totalElements, (int) Math.ceil((double) totalElements / size), postForms)
                        ));
    }

    @Override
    public Mono<PostPageForm> getPostPageByPostId(ServerHttpRequest request, String postId) {
        String authorId = CookieUtils.getUserId(request);
        return readPostById(postId)
                .flatMap(post -> {
                    post.visitPost();
                    return postRepository.save(post);
                })
                .flatMap(post -> commentService.readCommentsByPostId(post.getId(),authorId)
                        .map(commentWithReplies -> new PostPageForm(post, commentWithReplies,authorId)));
    }

    @Override
    public Mono<Void> deletePostById(String id) {
        return deleteService.deletePost(id);
    }

}