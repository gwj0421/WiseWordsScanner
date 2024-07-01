package com.example.services;

import com.example.dao.Post;
import com.example.dto.PageablePostsResponse;
import com.example.dto.PostForm;
import com.example.dto.PostPageForm;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {
    Mono<String> createPost(PostForm postForm, ServerHttpRequest request);
    Mono<Post> readPostById(String id);
    Flux<Post> readPostsByAuthorId(String userId);
//    Mono<String> findAuthorUserIdByAuthorId(String authorId);
    Mono<PageablePostsResponse> getPostsByPage(int pageNumber, int pageSize);
    Mono<PageablePostsResponse> getPostsBySearch(String keyword,int page, int size);
//    Mono<PostForm> getPostDetailById(String id);
    Mono<PostPageForm> getPostPageByPostId(ServerHttpRequest request,String postId);
    Mono<Void> deletePostById(String id);
    
}
