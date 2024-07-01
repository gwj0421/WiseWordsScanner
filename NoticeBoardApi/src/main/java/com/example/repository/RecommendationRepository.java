package com.example.repository;

import com.example.dao.Recommendation;
import com.example.dto.TargetType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface RecommendationRepository extends ReactiveMongoRepository<Recommendation, String> {
    Mono<Recommendation> findRecommendationByTargetTypeAndTargetIdAndUserId(TargetType targetType, String targetId, String userId);

    Mono<Void> deleteRecommendationByTargetTypeAndTargetIdAndUserId(TargetType targetType, String targetId, String userId);
}
