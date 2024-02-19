package com.example.repository;

import com.example.dao.Recommendation;
import com.example.dto.TargetType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface RecommendationRepository extends ReactiveMongoRepository<Recommendation, String> {
    Flux<Recommendation> findRecommendationsByTargetTypeAndTargetIdAndUserId(TargetType targetType, String targetId, String userId);
}
