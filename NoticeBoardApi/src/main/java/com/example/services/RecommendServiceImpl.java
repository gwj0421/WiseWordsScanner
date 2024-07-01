package com.example.services;

import com.example.dao.Comment;
import com.example.dao.Recommendation;
import com.example.dto.Recommendable;
import com.example.dto.TargetType;
import com.example.exception.error.RecommendException;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import com.example.repository.RecommendationRepository;
import com.example.repository.ReplyRepository;
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
    private final RecommendationRepository recommendationRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

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
    public Mono<ResponseEntity<Void>> recommendOnly(ReactiveMongoRepository<Recommendable, String> targetRepository, TargetType targetType, String targetId, String recommenderId) {
        return recommendationRepository.findRecommendationByTargetTypeAndTargetIdAndUserId(targetType, targetId, recommenderId)
                .flatMap(recommendation -> recommendationRepository.deleteById(recommendation.getId())
                        .then(targetRepository.findById(targetId)
                                .flatMap(entity -> {
                                    entity.getRecommendUserIds().remove(recommenderId);
                                    return targetRepository.save(entity);
                                })))
                .switchIfEmpty(Mono.defer(() -> {
                            Recommendation recommendation = new Recommendation(recommenderId, targetType, targetId, true);
                            return recommendationRepository.save(recommendation)
                                    .flatMap(newRecommend -> targetRepository.findById(targetId)
                                            .flatMap(entity -> {
                                                Comment comment = (Comment) entity;
                                                comment.getRecommendUserIds().add(recommenderId);
                                                return commentRepository.save(comment);
//                                                entity.getRecommendUserIds().add(recommenderId);
//                                                log.info("gwj : please");
//                                                return targetRepository.save(entity);
                                            }));
                        }
                ))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @Override
    public Mono<ResponseEntity<Void>> recommendWithNotRecommend(ReactiveMongoRepository<Recommendable, String> targetRepository, TargetType targetType, String targetId, String recommenderId, boolean userRecommend) {
        return recommendationRepository.findRecommendationByTargetTypeAndTargetIdAndUserId(targetType, targetId, recommenderId)
                .flatMap(recommendation -> {
                    if (!recommendation.isRecommend() ^ userRecommend) {
                        return Mono.error(new RecommendException("recommend(duplicated recommend)"));
                    } else {
                        recommendation.switchRecommend();
                        return recommendationRepository.save(recommendation)
                                .flatMap(changedRecommend -> targetRepository.findById(targetId)
                                        .flatMap(entity -> {
                                            if (changedRecommend.isRecommend()) {
                                                entity.getNotRecommendUserIds().remove(recommenderId);
                                                entity.getRecommendUserIds().add(recommenderId);
                                            } else {
                                                entity.getRecommendUserIds().remove(recommenderId);
                                                entity.getNotRecommendUserIds().add(recommenderId);
                                            }
                                            return targetRepository.save(entity);
                                        }));
                    }
                })
                .switchIfEmpty(Mono.defer(() -> targetRepository.findById(targetId))
                        .flatMap(post -> {
                            Recommendation recommendation = new Recommendation(recommenderId, targetType, targetId, userRecommend);
                            return recommendationRepository.save(recommendation)
                                    .flatMap(newRecommend -> targetRepository.findById(targetId)
                                            .flatMap(entity -> {
                                                if (newRecommend.isRecommend()) {
                                                    entity.getRecommendUserIds().add(recommenderId);
                                                } else {
                                                    entity.getNotRecommendUserIds().add(recommenderId);
                                                }
                                                return targetRepository.save(entity);
                                            }));
                        }))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteRecommendWithNotRecommend(ReactiveMongoRepository<Recommendable, String> targetRepository, TargetType targetType, String targetId, String recommenderId, boolean isRecommend) {
        return recommendationRepository.deleteRecommendationByTargetTypeAndTargetIdAndUserId(targetType, targetId, recommenderId)
                .then(targetRepository.findById(targetId).flatMap(entity -> {
                    if (isRecommend) {
                        entity.getRecommendUserIds().remove(recommenderId);
                    } else {
                        entity.getNotRecommendUserIds().remove(recommenderId);
                    }
                    return targetRepository.save(entity);
                }))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @Override
    public Mono<ResponseEntity<Void>> recommendPost(String postId, String recommenderUserId, boolean isRecommend) {
        return recommendationRepository.findRecommendationByTargetTypeAndTargetIdAndUserId(TargetType.POST, postId, recommenderUserId)
                .flatMap(recommendation -> {
                    if (!recommendation.isRecommend() ^ isRecommend) {
                        return Mono.error(new RecommendException("recommend(duplicated recommend)"));
                    } else {
                        recommendation.switchRecommend();
                        return recommendationRepository.save(recommendation)
                                .flatMap(changedRecommend -> postRepository.findById(postId)
                                        .flatMap(entity -> {
                                            if (changedRecommend.isRecommend()) {
                                                entity.getNotRecommendUserIds().remove(recommenderUserId);
                                                entity.getRecommendUserIds().add(recommenderUserId);
                                            } else {
                                                entity.getRecommendUserIds().remove(recommenderUserId);
                                                entity.getNotRecommendUserIds().add(recommenderUserId);
                                            }
                                            return postRepository.save(entity);
                                        }));
                    }
                })
                .switchIfEmpty(Mono.defer(() -> postRepository.findById(postId))
                        .flatMap(post -> {
                            Recommendation recommendation = new Recommendation(recommenderUserId, TargetType.POST, postId, isRecommend);
                            return recommendationRepository.save(recommendation)
                                    .flatMap(newRecommend -> postRepository.findById(postId)
                                            .flatMap(entity -> {
                                                if (newRecommend.isRecommend()) {
                                                    entity.getRecommendUserIds().add(recommenderUserId);
                                                } else {
                                                    entity.getNotRecommendUserIds().add(recommenderUserId);
                                                }
                                                return postRepository.save(entity);
                                            }));
                        }))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @Override
    public Mono<ResponseEntity<Void>> recommendComment(String commentId, String recommenderUserId) {
        return recommendationRepository.findRecommendationByTargetTypeAndTargetIdAndUserId(TargetType.COMMENT, commentId, recommenderUserId)
                .flatMap(recommendation -> recommendationRepository.deleteById(recommendation.getId())
                        .then(commentRepository.findById(commentId)
                                .flatMap(entity -> {
                                    entity.getRecommendUserIds().remove(recommenderUserId);
                                    return commentRepository.save(entity);
                                })))
                .switchIfEmpty(Mono.defer(() -> recommendationRepository.save(new Recommendation(recommenderUserId, TargetType.COMMENT, commentId, true))
                                .flatMap(recommendation -> commentRepository.findById(commentId))
                                .flatMap(comment -> {
                                    comment.getRecommend().add(recommenderUserId);
                                    return commentRepository.save(comment);
                                })
//                                    .flatMap(newRecommend -> commentRepository.findById(commentId)
//                                            .flatMap(entity -> {
//                                                Comment comment = (Comment) entity;
//                                                comment.getRecommendUserIds().add(recommenderUserId);
//                                                return commentRepository.save(comment);
//                                            }));

                ))
                .then(Mono.just(ResponseEntity.ok().build()));
    }

    @Override
    public Mono<ResponseEntity<Void>> recommendReply(String replyId, String recommenderUserId) {
        return null;
    }

}
