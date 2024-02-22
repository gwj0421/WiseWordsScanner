package com.example.services;

import com.example.dao.Post;
import com.example.dto.PageablePostsResponse;
import com.example.dto.PostForm;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {
    Mono<Post> createPost(Post post);
    Mono<Post> createPost(PostForm postForm);
    Mono<Post> readPostById(String id);
    Flux<Post> readPostsByAuthorId(String userId);
    Mono<Void> recommendPost(String postId, String recommenderId,boolean userRecommend);
    Mono<PageablePostsResponse> getPostsByPage(int pageNumber, int pageSize);

    Mono<PostForm> getPostDetailById(String id);
    Mono<Void> deletePostById(String id);
}
