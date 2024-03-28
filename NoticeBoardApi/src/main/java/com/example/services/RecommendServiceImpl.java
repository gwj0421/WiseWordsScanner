package com.example.services;

import com.example.dao.Recommendation;
import com.example.dto.Recommendable;
import com.example.dto.TargetType;
import com.example.exception.error.RecommendException;
import com.example.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Mono<ResponseEntity<Integer>> initCheck(ReactiveMongoRepository<Recommendable, String> targetRepository, TargetType targetType, String targetId, String recommenderId) {
        return recommendationRepository.findRecommendationByTargetTypeAndTargetIdAndUserId(targetType, targetId, recommenderId)
                .map(recommendation -> {
                    if (recommendation.isRecommend()) {
                        return 1;
                    }
                    return -1;
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just(0)))
                .map(code -> ResponseEntity.status(HttpStatus.OK).body(code));
    }

    @Override
    public Mono<ResponseEntity<Void>> recommend(ReactiveMongoRepository<Recommendable, String> targetRepository, TargetType targetType, String targetId, String recommenderId, boolean userRecommend) {
        return recommendationRepository.findRecommendationsByTargetTypeAndTargetIdAndUserId(targetType, targetId, recommenderId)
                .flatMap(recommendation -> {
                    if (!recommendation.isRecommend() ^ userRecommend) {
                        return Mono.error(new RecommendException("recommend(duplicated recommend)"));
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
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteRecommendPost(ReactiveMongoRepository<Recommendable, String> targetRepository, TargetType targetType, String targetId, String recommenderId, boolean isRecommend) {
        log.info("gwj");
        return recommendationRepository.deleteRecommendationByTargetTypeAndTargetIdAndUserId(targetType, targetId, recommenderId)
                .then(targetRepository.findById(targetId).flatMap(entity -> {
                    if (isRecommend) {
                        entity.getRecommendUserIds().get(RECOMMEND_KEY).remove(recommenderId);
                    } else {
                        entity.getRecommendUserIds().get(UN_RECOMMEND_KEY).remove(recommenderId);
                    }
                    return targetRepository.save(entity);
                }))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

}
