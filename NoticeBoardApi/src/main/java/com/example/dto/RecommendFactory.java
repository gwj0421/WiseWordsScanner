package com.example.dto;

import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import com.example.repository.ReplyRepository;
import com.example.services.RecommendService;
import com.example.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class RecommendFactory {
    private final RecommendService recommendService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;

    public ReactiveMongoRepository selectRepository(TargetType targetType) {
        return switch (targetType) {
            case POST -> postRepository;
            case COMMENT -> commentRepository;
            case REPLY -> replyRepository;
            case NONE -> null;
        };
    }

    public Mono<ResponseEntity<Integer>> initCheck(ServerHttpRequest request, String repositoryName, String targetId) {
        TargetType targetType = TargetType.findTargetType(repositoryName);
        return recommendService.initCheck(selectRepository(targetType), targetType, targetId, CookieUtils.getUserId(request));
    }

    public Mono<ResponseEntity<Void>> recommend(ServerHttpRequest request, String repositoryName, String targetId, boolean isRecommend) {
        TargetType targetType = TargetType.findTargetType(repositoryName);
        if (targetType.equals(TargetType.POST)) {
            return recommendService.recommendPost(targetId, CookieUtils.getUserId(request), isRecommend);
        } else if (targetType.equals(TargetType.COMMENT)) {
            return recommendService.recommendComment(targetId, CookieUtils.getUserId(request));
        } else if (targetType.equals(TargetType.REPLY)) {
            return recommendService.recommendReply(targetId, CookieUtils.getUserId(request));
        }
        return Mono.just(ResponseEntity.badRequest().build());
//        if (targetType.isWithNotRecommend()) {
//            if (targetType.equals(TargetType.COMMENT)) {
//
//            }
//            return recommendService.recommendWithNotRecommend(selectRepository(targetType), targetType, targetId, CookieUtils.getUserId(request), isRecommend);
//        }
//        return recommendService.recommendOnly(selectRepository(targetType), targetType, targetId, CookieUtils.getUserId(request));
    }

    public Mono<ResponseEntity<Void>> deleteRecommend(ServerHttpRequest request, String repositoryName, String targetId, boolean isRecommend) {
        TargetType targetType = TargetType.findTargetType(repositoryName);
        if (targetType.isWithNotRecommend()) {
            return recommendService.deleteRecommendWithNotRecommend(selectRepository(targetType), targetType, targetId, CookieUtils.getUserId(request), isRecommend);
        }
        return recommendService.recommendOnly(selectRepository(targetType), targetType, targetId, CookieUtils.getUserId(request));
    }

}
