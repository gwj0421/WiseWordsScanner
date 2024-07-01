package com.example.controller;

import com.example.dto.RecommendFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/reco")
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendFactory recommendFactory;

//    @GetMapping("/{targetName}/init/{targetId}")
//    public Mono<ResponseEntity<Integer>> init(ServerHttpRequest request,
//                                                  @PathVariable String targetName, @PathVariable String targetId) {
//        return recommendFactory.initCheck(request, targetName, targetId);
//    }

    @GetMapping("/{targetName}/{targetId}")
    public Mono<ResponseEntity<Void>> recommend(ServerHttpRequest request,
                                                       @PathVariable String targetName, @PathVariable String targetId,
                                                       @RequestParam(value = "isRecommend",required = false, defaultValue = "false") Boolean isRecommend) {
        return recommendFactory.recommend(request, targetName, targetId, isRecommend);
    }

    @DeleteMapping("/{targetName}/{targetId}")
    public Mono<ResponseEntity<Void>> deleteRecommend(ServerHttpRequest request,
                                                          @PathVariable String targetName,@PathVariable String targetId,
                                                          @RequestParam(value = "isRecommend",required = false, defaultValue = "false") Boolean isRecommend) {
        return recommendFactory.deleteRecommend(request, targetName, targetId, isRecommend);
    }
}
