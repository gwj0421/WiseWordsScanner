package com.example.services;

import com.example.dao.Reply;
import com.example.dto.ReplyForm;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReplyService {
    Mono<Reply> createReply(Reply reply);

    Mono<String> createReply(ServerHttpRequest request, ReplyForm replyForm);

    Mono<Reply> readReplyById(String id);

    Flux<ReplyForm> readReplyByCommentId(String commentId,String authorId);

    Mono<Void> deleteReplyById(String id);
}
