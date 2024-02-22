package com.example.services;

import com.example.dao.Recommendation;
import com.example.dto.Recommendable;
import com.example.dto.TargetType;
import com.example.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendServiceImpl implements RecommendService {
    public static final String RECOMMEND_KEY = "recommend";
    public static final String UN_RECOMMEND_KEY = "unRecommend";
    private final RecommendationRepository recommendationRepository;
    @Override
    public Mono<Void> recommend(ReactiveMongoRepository<Recommendable,String> targetRepository, TargetType targetType, String targetId, String recommenderId, boolean userRecommend) {
        return recommendationRepository.findRecommendationsByTargetTypeAndTargetIdAndUserId(targetType, targetId, recommenderId)
                .flatMap(recommendation -> {
                    if (!recommendation.isRecommend() ^ userRecommend) {
                        return Mono.error(new RuntimeException("already recommended or unRecommended " + targetType));
                    } else {
                        recommendation.switchRecommend();
                        return recommendationRepository.save(recommendation)
                                .then(targetRepository.findById(targetId))
                                .flatMap(entity -> {
                                    if (userRecommend) {
                                        entity.getRecommendUserIds().get(UN_RECOMMEND_KEY).remove(recommenderId);
                                        entity.getRecommendUserIds().get(RECOMMEND_KEY).add(recommenderId);
                                    } else {
                                        entity.getRecommendUserIds().get(RECOMMEND_KEY).remove(recommenderId);
                                        entity.getRecommendUserIds().get(UN_RECOMMEND_KEY).add(recommenderId);
                                    }
                                    return targetRepository.save(entity);
                                });
                    }
                })
                .switchIfEmpty(Mono.defer(() -> targetRepository.findById(targetId))
                        .flatMap(post -> {
                            Recommendation recommendation = new Recommendation(recommenderId, targetType, targetId, userRecommend);
                            return recommendationRepository.save(recommendation)
                                    .then(targetRepository.findById(targetId))
                                    .flatMap(entity -> {
                                        if (userRecommend) {
                                            entity.getRecommendUserIds().get(RECOMMEND_KEY).add(recommenderId);
                                        } else {
                                            entity.getRecommendUserIds().get(UN_RECOMMEND_KEY).add(recommenderId);
                                        }
                                        return targetRepository.save(entity);
                                    });
                        }))
                .then();
    }
}
