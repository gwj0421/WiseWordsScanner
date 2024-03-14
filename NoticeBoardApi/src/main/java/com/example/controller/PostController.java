package com.example.controller;

import com.example.dao.Post;
import com.example.dto.PageablePostsResponse;
import com.example.dto.PostForm;
import com.example.services.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Slf4j
public class PostController {
    private final PostService postService;

    @GetMapping("/id/{id}")
    public Mono<PostForm> getPostById(@PathVariable String id) {
        return postService.getPostDetailById(id);
    }

    @GetMapping("userId/{userId}")
    public Flux<Post> getPostByUserId(@PathVariable String userId) {
        return postService.readPostsByAuthorId(userId);
    }

    @GetMapping("/page")
    public Mono<PageablePostsResponse> getPostsByPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword",required = false) String keyword
    ) {
        if (keyword == null) {
            return postService.getPostsByPage(page, size);
        }
        return postService.getPostsBySearch(keyword, page, size);
    }

    @PostMapping()
    public Mono<PostForm> createPost(ServerHttpRequest request, @RequestBody PostForm postForm) {
        return postService.createPost(postForm, request);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deletePost(@PathVariable String id) {
        return postService.deletePostById(id);
    }
}
