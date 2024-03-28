package com.example.controller;

import com.example.dao.Reply;
import com.example.dto.ReplyForm;
import com.example.services.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reply")
public class ReplyController {
    private final ReplyService replyService;

    @GetMapping("id/{id}")
    public Mono<Reply> getReplyById(@PathVariable String id) {
        return replyService.readReplyById(id);
    }

    @GetMapping("commentId/{commentId}")
    public Flux<Reply> getReplyByCommentId(@PathVariable String commentId) {
        return replyService.readReplyByCommentId(commentId);
    }

    @PostMapping()
    public Mono<ReplyForm> createReply(ServerHttpRequest request, @RequestBody ReplyForm replyForm) {
        return replyService.createReply(request,replyForm);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteReply(@PathVariable String id) {
        return replyService.deleteReplyById(id);
    }
}

