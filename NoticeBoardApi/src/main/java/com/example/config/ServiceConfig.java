package com.example.config;

import com.example.repository.*;
import com.example.services.*;
import lombok.RequiredArgsConstructor;
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

    @Bean
    public PostService postService() {
        return new PostServiceImpl(loadBalancedWebClientBuilder,postRepository,recommendService(),formConverter(),deleteService());
    }

    @Bean
    public CommentService commentService() {
        return new CommentServiceImpl(commentRepository,recommendService(),formConverter(),deleteService());
    }

    @Bean
    public ReplyService replyService() {
        return new ReplyServiceImpl(replyRepository,recommendService(),formConverter(),deleteService());
    }

    @Bean
    public PageService pageService() {
        return new PageServiceImpl(postService(), commentService(), replyService());
    }

    @Bean
    public FormConverter formConverter() {
        return new FormConverterImpl(postRepository, commentRepository,loadBalancedWebClientBuilder);
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
