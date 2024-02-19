package com.example.config;

import com.example.repository.*;
import com.example.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ServiceConfig {
    private final SiteUserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final RecommendationRepository recommendationRepository;

    @Bean
    public SiteUserService userService() {
        return new SiteUserServiceImpl(userRepository,deleteService());
    }

    @Bean
    public PostService postService() {
        return new PostServiceImpl(postRepository,recommendService(),formConverter(),deleteService());
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
        return new FormConverterImpl(userRepository, postRepository, commentRepository);
    }

    @Bean
    public RecommendService recommendService() {
        return new RecommendServiceImpl(recommendationRepository);
    }

    @Bean
    public DeleteService deleteService() {
        return new DeleteServiceImpl(userRepository, postRepository, commentRepository, replyRepository, recommendationRepository);
    }
}
