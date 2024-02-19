package com.example.controller;

import com.example.dto.PostPageForm;
import com.example.services.PageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/page")
public class PageController {
    private final PageService pageService;

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PostPageForm>> getPostPage(@PathVariable String id) {
        Mono<PostPageForm> postPageInfo = pageService.getPostPageByPostId(id);
        return postPageInfo
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
}
