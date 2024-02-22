package com.example.controller;

import com.example.dao.Post;
import com.example.dto.PageablePostsResponse;
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
    public Mono<PostForm> getPostById(@PathVariable String id) {
        return postService.getPostDetailById(id).doOnNext(postForm -> log.info(postForm.getTitle()));
    }

    @GetMapping("userId/{userId}")
    public Flux<Post> getPostByUserId(@PathVariable String userId) {
        return postService.readPostsByAuthorId(userId);
    }

    @GetMapping("/page")
    public Mono<PageablePostsResponse> getPostsByPage(
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "10") int size
    ) {
        return postService.getPostsByPage(page,size);
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
