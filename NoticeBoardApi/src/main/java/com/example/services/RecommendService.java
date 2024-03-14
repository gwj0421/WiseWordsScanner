package com.example.services;

import com.example.dto.Recommendable;
import com.example.dto.TargetType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface RecommendService {
    Mono<Integer> initCheck(ReactiveMongoRepository<Recommendable,String> targetRepository, TargetType targetType, String targetId, String recommenderId);
    Mono<Void> recommend(ReactiveMongoRepository<Recommendable, String> targetRepository, TargetType targetType, String targetId, String recommenderId, boolean userRecommend);
}
