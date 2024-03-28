package com.example.services;

import com.example.dto.Recommendable;
import com.example.dto.TargetType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface RecommendService {
    Mono<ResponseEntity<Integer>> initCheck(ReactiveMongoRepository<Recommendable,String> targetRepository, TargetType targetType, String targetId, String recommenderId);
    Mono<ResponseEntity<Void>> recommend(ReactiveMongoRepository<Recommendable, String> targetRepository, TargetType targetType, String targetId, String recommenderId, boolean userRecommend);
    Mono<ResponseEntity<Void>> deleteRecommendPost(ReactiveMongoRepository<Recommendable,String> targetRepository, TargetType targetType, String targetId, String recommenderId,boolean isRecommend);
}
