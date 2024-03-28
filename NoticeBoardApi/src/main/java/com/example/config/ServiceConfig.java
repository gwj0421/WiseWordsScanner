package com.example.config;

import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import com.example.repository.RecommendationRepository;
import com.example.repository.ReplyRepository;
import com.example.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class ServiceConfig {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final RecommendationRepository recommendationRepository;
    private final WebClient.Builder loadBalancedWebClientBuilder;
    private final ReactiveCircuitBreakerFactory circuitBreakerFactory;

    @Bean
    public PostService postService() {
        return new PostServiceImpl(commentService(),replyService(),loadBalancedWebClientBuilder,postRepository,recommendService(),formConverter(),deleteService(),circuitBreakerFactory);
    }

    @Bean
    public CommentService commentService() {
        return new CommentServiceImpl(commentRepository,replyService(),recommendService(),formConverter(),deleteService());
    }

    @Bean
    public ReplyService replyService() {
        return new ReplyServiceImpl(replyRepository,recommendService(),formConverter(),deleteService());
    }

    @Bean
    public FormConverter formConverter() {
        return new FormConverterImpl(postRepository, commentRepository,loadBalancedWebClientBuilder,circuitBreakerFactory);
    }

    @Bean
    public RecommendService recommendService() {
        return new RecommendServiceImpl(recommendationRepository);
    }

    @Bean
    public DeleteService deleteService() {
        return new DeleteServiceImpl(postRepository, commentRepository, replyRepository, recommendationRepository);
    }

}
