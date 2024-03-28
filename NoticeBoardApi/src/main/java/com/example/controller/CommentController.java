package com.example.controller;

import com.example.dao.Comment;
import com.example.dto.CommentForm;
import com.example.dto.CommentWithReplies;
import com.example.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("id/{id}")
    public Mono<Comment> getCommentById(@PathVariable String id) {
        return commentService.readCommentById(id);
    }

    @GetMapping("userId/{userId}")
    public Flux<Comment> getPostByUserId(@PathVariable String userId) {
        return commentService.readCommentsByAuthorId(userId);
    }

    @GetMapping("postId/{postId}")
    public Mono<List<CommentWithReplies>> getPostByPostId(@PathVariable String postId) {
        return commentService.readCommentsByPostId(postId);
    }

    @PostMapping()
    public Mono<CommentForm> createPost(ServerHttpRequest request, @RequestBody CommentForm commentForm) {
        return commentService.createComment(request,commentForm);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteComment(@PathVariable String id) {
        return commentService.deleteCommentById(id);
    }
}
