package com.example.controller;

import com.example.dao.Post;
import com.example.dto.PostForm;
import com.example.services.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Slf4j
public class PostController {
    private final PostService postService;

    @GetMapping("id/{id}")
    public Mono<Post> getPostById(@PathVariable String id) {
        return postService.readPostById(id);
    }

    @GetMapping("userId/{userId}")
    public Flux<Post> getPostByUserId(@PathVariable String userId) {
        return postService.readPostsByAuthorId(userId);
    }

    @PostMapping()
    public Mono<Post> createPost(@RequestBody PostForm postForm) {
        return postService.createPost(postForm);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deletePost(@PathVariable String id) {
        return postService.deletePostById(id);
    }
}
