package com.example.services;

import com.example.config.ServiceConfig;
import com.example.config.WebClientConfig;
import com.example.dao.Comment;
import com.example.dao.Post;
import com.example.dao.Reply;
import com.example.dto.PostPageForm;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import com.example.repository.RecommendationRepository;
import com.example.repository.ReplyRepository;
import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@DataMongoTest
@Import({ServiceConfig.class, WebClientConfig.class, CircuitBreakerAutoConfiguration.class})
@Slf4j
class PostServiceTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private RecommendationRepository recommendationRepository;
    @Autowired
    private PostService postService;

    @BeforeEach
    @AfterEach
    void setUpAndTearDown() {
        postRepository.deleteAll().block();
        commentRepository.deleteAll().block();
        replyRepository.deleteAll().block();
        recommendationRepository.deleteAll().block();
    }

    @Test
    void Should_returnPostPage_When_givenPostAndUserAndCommentsAndReplies() {
        // given
        Post savedPost = postRepository.save(new Post("testAuthorId1", "testAuthorUserId1", "title", "testPostContent")).block();
        Comment savedComment1 = commentRepository.save(new Comment(savedPost, "testAuthorId2", "testAuthorUserId2", "testCommentContent")).block();
        Reply reply1_1 = replyRepository.save(new Reply(savedComment1, "testAuthorId1", "testAuthorUserId1", "testReplyContent1-1")).block();
        Reply reply1_2 = replyRepository.save(new Reply(savedComment1, "testAuthorId3", "testAuthorUserId3", "testReplyContent1-2")).block();
        Comment savedComment2 = commentRepository.save(new Comment(savedPost, "testAuthorId3", "testAuthorUserId3", "testCommentContent")).block();
        Reply reply2_1 = replyRepository.save(new Reply(savedComment2, "testAuthorId1", "testAuthorUserId1", "testReplyContent2-1")).block();
        Reply reply2_2 = replyRepository.save(new Reply(savedComment2, "testAuthorId2", "testAuthorUserId2", "testReplyContent2-2")).block();

        // when
        Mono<PostPageForm> createPostPage = postService.getPostPageByPostId(savedPost.getId());
        PostPageForm block = createPostPage.block();

        // then
        StepVerifier.create(createPostPage)
                .expectNextMatches(postPageForm -> postPageForm.getPost().getPostId().equals(savedPost.getId()) && postPageForm.getPost().getAuthorUserId().equals("testAuthorUserId1"))
                .verifyComplete();
        StepVerifier.create(commentRepository.findCommentsByPostId(savedPost.getId()))
                .expectNextMatches(comment -> comment.getId().equals(savedComment1.getId()))
                .expectNextMatches(comment -> comment.getId().equals(savedComment2.getId()))
                .verifyComplete();
        StepVerifier.create(replyRepository.findAllByCommentIdOrderByCreatedDate(savedComment1.getId()))
                .expectNextMatches(reply -> reply.getId().equals(reply1_1.getId()))
                .expectNextMatches(reply -> reply.getId().equals(reply1_2.getId()))
                .verifyComplete();
        StepVerifier.create(replyRepository.findAllByCommentIdOrderByCreatedDate(savedComment2.getId()))
                .expectNextMatches(reply -> reply.getId().equals(reply2_1.getId()))
                .expectNextMatches(reply -> reply.getId().equals(reply2_2.getId()))
                .verifyComplete();
    }
//    @Test
//    void Should_deleteUser_When_givenUserInfo() {
//        // given
//        SiteUser user = new SiteUser("testName", "testUserId", "testPassword", "testEmail@gamil.com");
//
//        // when
//        SiteUser saveUser = userRepository.save(user).block();
//        Post post = postService.createPost(new Post(saveUser, "title", "testContent")).block();
//        Mono<Void> deletePost = postService.deletePostById(post.getId());
//        Flux<Post> readPost = postService.readPostsByAuthorId(saveUser.getId());
//
//        // then
//        StepVerifier.create(deletePost)
//                .verifyComplete();
//        StepVerifier.create(readPost)
//                .expectNextCount(0)
//                .verifyComplete();
//    }
//
//    @Test
//    void Should_manageRecommendCnt_When_givenRecommendUser() {
//        // given
//        SiteUser savedUser1 = userRepository.save(new SiteUser("testName1", "testUserId", "testPassword", "testEmail@gamil.com")).block();
//        SiteUser savedUser2 = userRepository.save(new SiteUser("testName2", "testUserId", "testPassword", "testEmail@gamil.com")).block();
//        SiteUser savedUser3 = userRepository.save(new SiteUser("testName3", "testUserId", "testPassword", "testEmail@gamil.com")).block();
//        Post savePost1 = postService.createPost(new Post(savedUser1, "title1", "content1")).block();
//        Post savePost2 = postService.createPost(new Post(savedUser2, "title1", "content2")).block();
//        Post savePost3 = postService.createPost(new Post(savedUser3, "title1", "content3")).block();
//
//        // when
//        /*
//            testCase1 : 다수 추천 & 비추천
//            testCase2 : 중복 추천
//            testCase3 : 중복 비추천
//            testCase4 : 추천, 비추천 변경
//         */
//        Mono<Post> testCase1 = postService.recommendPost(savePost1.getId(), savedUser1.getId(), true)
//                .then(postService.recommendPost(savePost1.getId(), savedUser2.getId(), true))
//                .then(postService.recommendPost(savePost1.getId(), savedUser3.getId(), false))
//                .then(postService.readPostById(savePost1.getId()));
//        Mono<Void> testCase2 = postService.recommendPost(savePost2.getId(), savedUser1.getId(), true)
//                .then(postService.recommendPost(savePost2.getId(), savedUser1.getId(), true));
//        Mono<Void> testCase3 = postService.recommendPost(savePost2.getId(), savedUser2.getId(), false)
//                .then(postService.recommendPost(savePost2.getId(), savedUser2.getId(), false));
//        Mono<Post> testCase4 = postService.recommendPost(savePost3.getId(), savedUser1.getId(), true)
//                .then(postService.recommendPost(savePost3.getId(), savedUser1.getId(), false))
//                .then(postService.readPostById(savePost3.getId()));
//
//        // then
//        StepVerifier.create(testCase1)
//                .expectNextMatches(post -> post.getRecommendUserIds().get("recommend").size() == 2 && post.getRecommendUserIds().get("unRecommend").size() == 1)
//                .verifyComplete();
//        StepVerifier.create(testCase2)
//                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().startsWith("already recommended or unRecommended"))
//                .verify();
//        StepVerifier.create(testCase3)
//                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().startsWith("already recommended or unRecommended"))
//                .verify();
//        StepVerifier.create(testCase4)
//                .expectNextMatches(post -> post.getRecommendUserIds().get("recommend").isEmpty() && post.getRecommendUserIds().get("unRecommend").size() == 1)
//                .verifyComplete();
//    }
//
//    @Test
//    void Should_showPosts_When_givenPageNumAndPageSize() {
//        // given
//        int pageNumber = 0;
//        int pageSize = 10;
//        int totalPostCnt = 20;
//        int totalPageCnt = (int) Math.ceil(totalPostCnt / pageSize);
//
//        // when
//        Flux<Post> postFlux = userRepository.save(new SiteUser("testName1", "testUserId", "testPassword", "testEmail@gamil.com"))
//                .flatMapMany(user ->
//                        Flux.range(1, totalPostCnt)
//                                .concatMap(index -> postService.createPost(new Post(user, "title " + index, "content " + index))));
//
//        Mono<PageablePostsResponse> postsByPage = postService.getPostsByPage(pageNumber, pageSize);
//
//
//        // then
//        StepVerifier.create(postFlux)
//                .expectNextCount(totalPostCnt)
//                .verifyComplete();
//        StepVerifier.create(postsByPage)
//                .expectNextMatches(pageablePostsResponse -> {
//                    int startIdx = 1;
//                    for (PostForm post : pageablePostsResponse.getPosts()) {
//                        if (!post.getTitle().equals("title " + startIdx++)) {
//                            return false;
//                        }
//                    }
//                    return pageablePostsResponse.getPosts().size() == pageSize
//                            && pageablePostsResponse.getCurrentPage() == pageNumber
//                            && pageablePostsResponse.getPageSize() == pageSize
//                            && pageablePostsResponse.getTotalPages() == totalPageCnt
//                            && pageablePostsResponse.getTotalElements() == totalPostCnt;
//                })
//                .verifyComplete();
//    }
//
//    @Test
//    void Should_updatePost_When_givenNewPostInfo() {
//        // given
//        Post originalPost = userRepository.save(new SiteUser("testName1", "testUserId", "testPassword", "testEmail@gamil.com"))
//                .flatMap(user -> postService.createPost(new Post(user, "title", "content"))).block();
//        Instant originalCreatedDate = originalPost.getCreatedDate();
//        Instant modifiedDate = originalPost.getModifiedDate();
//
//        // when
//        originalPost.changeContent("changedContent");
//        Mono<Post> updatePost = postService.createPost(originalPost);
//
//        // then
//        StepVerifier.create(updatePost)
//                .expectNextMatches(post -> post.getCreatedDate().equals(originalCreatedDate) && post.getModifiedDate().isAfter(modifiedDate))
//                .verifyComplete();
//    }
}