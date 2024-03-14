package com.example.controller;

import com.example.dto.TargetType;
import com.example.repository.PostRepository;
import com.example.services.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/reco")
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;
    private final PostRepository postRepository;
    @GetMapping("/post/init/{postId}")
    public Mono<ResponseEntity<Integer>> initPost(ServerHttpRequest request, @PathVariable String postId) {
        String recommenderId = request.getCookies().get("Uid").get(0).getValue();
        return recommendService.initCheck((ReactiveMongoRepository) postRepository, TargetType.POST, postId, recommenderId)
                .map(code -> ResponseEntity.status(HttpStatus.OK).body(code));
    }

    @GetMapping("/post/{postId}")
    public Mono<ResponseEntity<Integer>> recommendPost(ServerHttpRequest request, @PathVariable String postId,
                                                       @RequestParam(value = "isRecommend") boolean isRecommend) {
        String recommenderId = request.getCookies().get("Uid").get(0).getValue();
        return recommendService.recommend((ReactiveMongoRepository) postRepository, TargetType.POST, postId, recommenderId, isRecommend)
                .onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
    }

}
