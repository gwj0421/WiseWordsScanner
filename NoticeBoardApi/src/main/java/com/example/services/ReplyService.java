package com.example.services;

import com.example.dao.Reply;
import com.example.dto.ReplyForm;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReplyService {
    Mono<Reply> createReply(Reply reply);
    Mono<Reply> createReply(ReplyForm replyForm);
    Mono<Reply> readReplyById(String id);
    Flux<Reply> readReplyByCommentId(String id);
    Mono<Void> recommend(String replyId, String recommenderId, boolean userRecommend);
    Mono<Void> deleteReplyById(String id);
}
