package com.example.services;

import com.example.dto.Recommendable;
import com.example.dto.TargetType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface RecommendService {
    Mono<ResponseEntity<Integer>> initCheck(ReactiveMongoRepository<Recommendable, String> targetRepository, TargetType targetType, String targetId, String recommenderId);

    Mono<ResponseEntity<Void>> recommendOnly(ReactiveMongoRepository<Recommendable, String> targetRepository, TargetType targetType, String targetId, String recommenderId);

    Mono<ResponseEntity<Void>> recommendWithNotRecommend(ReactiveMongoRepository<Recommendable, String> targetRepository, TargetType targetType, String targetId, String recommenderId, boolean userRecommend);

    Mono<ResponseEntity<Void>> deleteRecommendWithNotRecommend(ReactiveMongoRepository<Recommendable, String> targetRepository, TargetType targetType, String targetId, String recommenderId, boolean isRecommend);

    Mono<ResponseEntity<Void>> recommendPost(String postId, String recommenderUserId, boolean isRecommend);

    Mono<ResponseEntity<Void>> recommendComment(String commentId, String recommenderUserId);

    Mono<ResponseEntity<Void>> recommendReply(String replyId, String recommenderUserId);

}
