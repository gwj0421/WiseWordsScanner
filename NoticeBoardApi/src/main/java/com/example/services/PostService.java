package com.example.services;

import com.example.dao.Post;
import com.example.dto.PageablePostsResponse;
import com.example.dto.PostForm;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {
    Mono<Post> createPost(Post post);
    Mono<PostForm> createPost(PostForm postForm, ServerHttpRequest request);
    Mono<Post> readPostById(String id);
    Flux<Post> readPostsByAuthorId(String userId);
    Mono<String> findAuthorUserIdByAuthorId(String authorId);
    Mono<Void> recommendPost(String postId, String recommenderId,boolean userRecommend);
    Mono<PageablePostsResponse> getPostsByPage(int pageNumber, int pageSize);
    Mono<PageablePostsResponse> getPostsBySearch(String keyword,int page, int size);


    Mono<PostForm> getPostDetailById(String id);
    Mono<Void> deletePostById(String id);
}
