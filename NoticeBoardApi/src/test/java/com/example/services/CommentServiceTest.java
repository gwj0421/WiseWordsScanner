package com.example.services;

import com.example.config.ServiceConfig;
import com.example.dao.Comment;
import com.example.dao.Post;
import com.example.dao.SiteUser;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import com.example.repository.SiteUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;

@DataMongoTest
@Import(ServiceConfig.class)
@Slf4j
class CommentServiceTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    SiteUserRepository userRepository;
    @Autowired
    PostRepository postRepository;

    @BeforeEach
    @AfterEach
    void setUpAndTearDown() {
        postRepository.deleteAll().block();
        userRepository.deleteAll().block();
        commentRepository.deleteAll().block();
    }

    @Test
    void Should_saveComment_When_givenCommentContext() {
        // given
        SiteUser user = new SiteUser("testName", "testUserId", "testPassword", "testEmail@gamil.com");
        SiteUser savedUser = userRepository.save(user).block();
        Post savedPost = postRepository.save(new Post(savedUser, "testPostContent")).block();

        // when
        Mono<Comment> createComment = commentService.createComment(new Comment(savedUser, savedPost, "testCommentContent"));

        // then
        StepVerifier.create(createComment)
                .expectNextMatches(retrievedComment -> retrievedComment.getId() != null
                        && retrievedComment.getAuthor().getId().equals(savedUser.getId())
                        && retrievedComment.getPost().getId().equals(savedPost.getId()))
                .verifyComplete();
    }

    @Test
    void Should_returnComment_When_givenAuthorIdOrPostId() {
        // given
        SiteUser savedUser = userRepository.save(new SiteUser("testName", "testUserId", "testPassword", "testEmail@gamil.com")).block();
        Post savedPost = postRepository.save(new Post(savedUser, "testPostContent")).block();

        // when
        Flux<Comment> commentFlux = Flux.range(1, 3).flatMap(index -> commentService.createComment(new Comment(savedUser, savedPost, "content " + index)));
        Flux<Comment> expectedCommentsByAuthorId = commentService.readCommentsByAuthorId(savedUser.getId());
        Flux<Comment> expectedCommentsByPostId = commentService.readCommentsByPostId(savedPost.getId());

        // then
        Set<String> comments = new HashSet<>();
        StepVerifier.create(commentFlux)
                .thenConsumeWhile(comment -> {
                    comments.add(comment.getId());
                    return comment.getAuthor().getId().equals(savedUser.getId()) && comment.getPost().getId().equals(savedPost.getId());
                })
                .verifyComplete();
        Set<String> commentsByAuthorId = new HashSet<>();
        Set<String> commentsByPostId = new HashSet<>();
        StepVerifier.create(expectedCommentsByAuthorId)
                .thenConsumeWhile(comment -> commentsByAuthorId.add(comment.getId()))
                .verifyComplete();
        StepVerifier.create(expectedCommentsByPostId)
                .thenConsumeWhile(comment -> commentsByPostId.add(comment.getId()))
                .verifyComplete();
        for (String comment : comments) {
            Assertions.assertThat(commentsByAuthorId.contains(comment) && commentsByPostId.contains(comment)).isTrue();
        }
    }

    @Test
    void Should_returnComments_When_givenManyAuthor() {
        // given
        SiteUser user1 = new SiteUser("testName", "testUserId1", "testPassword1", "testEmail1@gamil.com");
        SiteUser user2 = new SiteUser("testName2", "testUserId2", "testPassword2", "testEmail2@gamil.com");
        SiteUser savedUser1 = userRepository.save(user1).block();
        SiteUser savedUser2 = userRepository.save(user2).block();
        Post savedPost = postRepository.save(new Post(savedUser1, "testPostContent")).block();

        // when
        Flux<Comment> comment = commentService.createComment(new Comment(savedUser1, savedPost, "testContent1"))
                .then(commentService.createComment(new Comment(savedUser2, savedPost, "testContent2")))
                .thenMany(commentService.readCommentsByPostId(savedPost.getId()));

        // then
        StepVerifier.create(comment)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void Should_deleteComment_When_givenCommentId() {
        // given
        SiteUser user = new SiteUser("testName", "testUserId", "testPassword", "testEmail@gamil.com");
        SiteUser savedUser = userRepository.save(user).block();
        Post savedPost = postRepository.save(new Post(savedUser, "testPostContent")).block();

        // when
        Mono<Void> deleteComment = commentService.createComment(new Comment(savedUser, savedPost, "testCommentContent"))
                .flatMap(comment -> commentService.deleteCommentById(comment.getId()));

        // then
        StepVerifier.create(deleteComment).verifyComplete();
        StepVerifier.create(commentService.readCommentsByAuthorId(savedUser.getId()))
                .expectNextCount(0)
                .verifyComplete();
    }


}