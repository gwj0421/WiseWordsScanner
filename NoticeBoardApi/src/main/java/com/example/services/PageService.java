package com.example.services;

import com.example.dto.PostPageForm;
import reactor.core.publisher.Mono;

public interface PageService {
    Mono<PostPageForm> getPostPageByPostId(String postId);
}
