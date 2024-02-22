package com.example.services;

import com.example.config.ServiceConfig;
import com.example.dao.Post;
import com.example.dao.SiteUser;
import com.example.dto.PageablePostsResponse;
import com.example.dto.PostForm;
import com.example.repository.PostRepository;
import com.example.repository.RecommendationRepository;
import com.example.repository.SiteUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@DataMongoTest
@Import({ServiceConfig.class})
@Slf4j
class PostServiceTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private SiteUserRepository userRepository;
    @Autowired
    private RecommendationRepository recommendationRepository;
    @Autowired
    private PostService postService;


    @BeforeEach
    @AfterEach
    void setUpAndTearDown() {
        postRepository.deleteAll().block();
        userRepository.deleteAll().block();
        recommendationRepository.deleteAll().block();
    }

    @Test
    void Should_returnPost_When_givenPostInfo() {
        // given
        SiteUser author = new SiteUser("testName", "testUserId", "testPassword", "testEmail@gamil.com");

        // when
        Mono<Post> newPost = userRepository.save(author).flatMap(user -> postService.createPost(new Post(user, "title", "testContent")));

        // then
        StepVerifier.create(newPost)
                .expectNextMatches(retrievedPosts -> retrievedPosts.getAuthor().getUserId().equals("testUserId") && retrievedPosts.getContent().equals("testContent"))
                .verifyComplete();
    }

    @Test
    void Should_returnPosts_When_givenPostsInfo() {
        // given
        SiteUser author = new SiteUser("testName", "testUserId", "testPassword", "testEmail@gamil.com");

        // when
        SiteUser user = userRepository.save(author).block();
        Set<String> postIds = new HashSet<>();
        Flux<Post> newPosts = Flux.range(1, 3).flatMap(index -> postService.createPost(new Post(user, "title" + index, "content " + index)));

        // then
        StepVerifier.create(newPosts)
                .thenConsumeWhile(post -> {
                    postIds.add(post.getId());
                    return postIds.size() < 4;
                })
                .verifyComplete();

        StepVerifier.create(postService.readPostsByAuthorId(user.getId()))
                .thenConsumeWhile(post -> postIds.contains(post.getId()))
                .verifyComplete();
    }

    @Test
    void Should_deleteUser_When_givenUserInfo() {
        // given
        SiteUser user = new SiteUser("testName", "testUserId", "testPassword", "testEmail@gamil.com");

        // when
        SiteUser saveUser = userRepository.save(user).block();
        Post post = postService.createPost(new Post(saveUser, "title", "testContent")).block();
        Mono<Void> deletePost = postService.deletePostById(post.getId());
        Flux<Post> readPost = postService.readPostsByAuthorId(saveUser.getId());

        // then
        StepVerifier.create(deletePost)
                .verifyComplete();
        StepVerifier.create(readPost)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void Should_manageRecommendCnt_When_givenRecommendUser() {
        // given
        SiteUser savedUser1 = userRepository.save(new SiteUser("testName1", "testUserId", "testPassword", "testEmail@gamil.com")).block();
        SiteUser savedUser2 = userRepository.save(new SiteUser("testName2", "testUserId", "testPassword", "testEmail@gamil.com")).block();
        SiteUser savedUser3 = userRepository.save(new SiteUser("testName3", "testUserId", "testPassword", "testEmail@gamil.com")).block();
        Post savePost1 = postService.createPost(new Post(savedUser1, "title1", "content1")).block();
        Post savePost2 = postService.createPost(new Post(savedUser2, "title1", "content2")).block();
        Post savePost3 = postService.createPost(new Post(savedUser3, "title1", "content3")).block();

        // when
        /*
            testCase1 : 다수 추천 & 비추천
            testCase2 : 중복 추천
            testCase3 : 중복 비추천
            testCase4 : 추천, 비추천 변경
         */
        Mono<Post> testCase1 = postService.recommendPost(savePost1.getId(), savedUser1.getId(), true)
                .then(postService.recommendPost(savePost1.getId(), savedUser2.getId(), true))
                .then(postService.recommendPost(savePost1.getId(), savedUser3.getId(), false))
                .then(postService.readPostById(savePost1.getId()));
        Mono<Void> testCase2 = postService.recommendPost(savePost2.getId(), savedUser1.getId(), true)
                .then(postService.recommendPost(savePost2.getId(), savedUser1.getId(), true));
        Mono<Void> testCase3 = postService.recommendPost(savePost2.getId(), savedUser2.getId(), false)
                .then(postService.recommendPost(savePost2.getId(), savedUser2.getId(), false));
        Mono<Post> testCase4 = postService.recommendPost(savePost3.getId(), savedUser1.getId(), true)
                .then(postService.recommendPost(savePost3.getId(), savedUser1.getId(), false))
                .then(postService.readPostById(savePost3.getId()));

        // then
        StepVerifier.create(testCase1)
                .expectNextMatches(post -> post.getRecommendUserIds().get("recommend").size() == 2 && post.getRecommendUserIds().get("unRecommend").size() == 1)
                .verifyComplete();
        StepVerifier.create(testCase2)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().startsWith("already recommended or unRecommended"))
                .verify();
        StepVerifier.create(testCase3)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().startsWith("already recommended or unRecommended"))
                .verify();
        StepVerifier.create(testCase4)
                .expectNextMatches(post -> post.getRecommendUserIds().get("recommend").isEmpty() && post.getRecommendUserIds().get("unRecommend").size() == 1)
                .verifyComplete();
    }

    @Test
    void Should_showPosts_When_givenPageNumAndPageSize() {
        // given
        int pageNumber = 0;
        int pageSize = 10;
        int totalPostCnt = 20;
        int totalPageCnt = (int) Math.ceil(totalPostCnt / pageSize);

        // when
        Flux<Post> postFlux = userRepository.save(new SiteUser("testName1", "testUserId", "testPassword", "testEmail@gamil.com"))
                .flatMapMany(user ->
                        Flux.range(1, totalPostCnt)
                                .concatMap(index -> postService.createPost(new Post(user, "title " + index, "content " + index))));

        Mono<PageablePostsResponse> postsByPage = postService.getPostsByPage(pageNumber, pageSize);


        // then
        StepVerifier.create(postFlux)
                .expectNextCount(totalPostCnt)
                .verifyComplete();
        StepVerifier.create(postsByPage)
                .expectNextMatches(pageablePostsResponse -> {
                    int startIdx = 1;
                    for (PostForm post : pageablePostsResponse.getPosts()) {
                        if (!post.getTitle().equals("title " + startIdx++)) {
                            return false;
                        }
                    }
                    return pageablePostsResponse.getPosts().size() == pageSize
                            && pageablePostsResponse.getCurrentPage() == pageNumber
                            && pageablePostsResponse.getPageSize() == pageSize
                            && pageablePostsResponse.getTotalPages() == totalPageCnt
                            && pageablePostsResponse.getTotalElements() == totalPostCnt;
                })
                .verifyComplete();
    }

    @Test
    void Should_updatePost_When_givenNewPostInfo() {
        // given
        Post originalPost = userRepository.save(new SiteUser("testName1", "testUserId", "testPassword", "testEmail@gamil.com"))
                .flatMap(user -> postService.createPost(new Post(user, "title", "content"))).block();
        Instant originalCreatedDate = originalPost.getCreatedDate();
        Instant modifiedDate = originalPost.getModifiedDate();

        // when
        originalPost.changeContent("changedContent");
        Mono<Post> updatePost = postService.createPost(originalPost);

        // then
        StepVerifier.create(updatePost)
                .expectNextMatches(post -> post.getCreatedDate().equals(originalCreatedDate) && post.getModifiedDate().isAfter(modifiedDate))
                .verifyComplete();
    }
}