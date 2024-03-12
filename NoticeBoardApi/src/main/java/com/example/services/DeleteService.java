package com.example.services;

import reactor.core.publisher.Mono;

public interface DeleteService {
    Mono<Void> deletePost(String id);
    Mono<Void> deleteComment(String id);
    Mono<Void> deleteReply(String id);
    Mono<Void> deleteRecommend(String id);
}
