package com.example.services;

import com.example.dao.Comment;
import com.example.dao.Post;
import com.example.dao.Reply;
import com.example.dto.CommentForm;
import com.example.dto.PostForm;
import com.example.dto.ReplyForm;
import com.example.exception.error.SiteUserApiServerException;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormConverterImpl implements FormConverter {
    private static final String USER_MANAGE_API_URI = "http://user-manage-api/user/id/";
    private static final String CIRCUIT_BREAKER_ID = "userManageApiCircuitBreaker";
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final WebClient.Builder loadBalancedWebClientBuilder;
    private final ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    @Override
    public Mono<Post> toPost(PostForm postForm, String authorId) {
        return loadBalancedWebClientBuilder.build().get().uri(USER_MANAGE_API_URI + authorId)
                .retrieve()
                .bodyToMono(String.class)
                .map(authorUserId -> new Post(authorId, authorUserId, postForm.getTitle(), postForm.getContent()))
                .transform(it -> reactiveCircuitBreakerFactory.create(CIRCUIT_BREAKER_ID)
                        .run(it, throwable -> Mono.error(new SiteUserApiServerException("toPost"))));
    }

    @Override
    public Mono<Comment> toComment(CommentForm commentForm, String authorId) {
        return loadBalancedWebClientBuilder.build().get().uri(USER_MANAGE_API_URI + authorId)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(authorUserId -> postRepository.findPostById(commentForm.getPostId())
                        .map(post -> new Comment(post,authorId,authorUserId, commentForm.getContent())))
                .transform(it -> reactiveCircuitBreakerFactory.create(CIRCUIT_BREAKER_ID)
                        .run(it, throwable -> Mono.error(new SiteUserApiServerException("toComment"))));
    }

    @Override
    public Mono<Reply> toReply(ReplyForm replyForm,String authorId) {
        return loadBalancedWebClientBuilder.build().get().uri(USER_MANAGE_API_URI + authorId)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(authorUserId -> commentRepository.findById(replyForm.getCommentId())
                        .map(comment -> new Reply(comment,authorId,authorUserId, replyForm.getReplyContent())))
                .transform(it -> reactiveCircuitBreakerFactory.create(CIRCUIT_BREAKER_ID)
                        .run(it, throwable -> Mono.error(new SiteUserApiServerException("toReply"))));
    }
}
