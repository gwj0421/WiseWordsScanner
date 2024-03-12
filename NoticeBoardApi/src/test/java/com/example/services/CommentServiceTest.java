package com.example.services;

import com.example.config.ServiceConfig;
import com.example.config.WebClientConfig;
import com.example.dao.Comment;
import com.example.dao.Post;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
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
@Import({ServiceConfig.class, WebClientConfig.class})
@Slf4j
class CommentServiceTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    PostRepository postRepository;

    @BeforeEach
    @AfterEach
    void setUpAndTearDown() {
        postRepository.deleteAll().block();
        commentRepository.deleteAll().block();
    }

    @Test
    void Should_saveComment_When_givenCommentContext() {
        // given
        String authorId = "testAuthorId";
        Post savedPost = postRepository.save(new Post(authorId, "testAuthorUserId", "title", "testPostContent")).block();

        // when
        Mono<Comment> createComment = commentService.createComment(new Comment(savedPost, authorId, "testAuthorUserId1", "testCommentContent"));

        // then
        StepVerifier.create(createComment)
                .expectNextMatches(retrievedComment -> retrievedComment.getId() != null
                        && retrievedComment.getAuthorId().equals(authorId)
                        && retrievedComment.getPost().getId().equals(savedPost.getId()))
                .verifyComplete();
    }

    @Test
    void Should_returnComment_When_givenAuthorIdOrPostId() {
        // given
        String authorId = "testAuthorId";
        Post savedPost = postRepository.save(new Post(authorId, "testAuthorUserId", "title", "testPostContent")).block();

        // when
        Flux<Comment> commentFlux = Flux.range(1, 3).flatMap(index -> commentService.createComment(new Comment(savedPost, authorId, "testAuthorUserId", "content " + index)));
        Flux<Comment> expectedCommentsByAuthorId = commentService.readCommentsByAuthorId(authorId);
        Flux<Comment> expectedCommentsByPostId = commentService.readCommentsByPostId(savedPost.getId());

        // then
        Set<String> comments = new HashSet<>();
        StepVerifier.create(commentFlux)
                .thenConsumeWhile(comment -> {
                    comments.add(comment.getId());
                    return comment.getAuthorId().equals(authorId) && comment.getPost().getId().equals(savedPost.getId());
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
        Post savedPost = postRepository.save(new Post("testAuthorId1", "testAuthorUserId1", "title", "testPostContent")).block();

        // when
        Flux<Comment> comment = commentService.createComment(new Comment(savedPost, "testAuthorId", "testAuthorUserId", "testContent1"))
                .then(commentService.createComment(new Comment(savedPost, "testAuthorId2", "testAuthorUserId2", "testContent2")))
                .thenMany(commentService.readCommentsByPostId(savedPost.getId()));

        // then
        StepVerifier.create(comment)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void Should_deleteComment_When_givenCommentId() {
        // given
        Post savedPost = postRepository.save(new Post("testAuthorId", "testAuthorUserId", "title", "testPostContent")).block();

        // when
        Mono<Void> deleteComment = commentService.createComment(new Comment(savedPost, "testAuthorId", "testAuthorUserId", "testCommentContent"))
                .flatMap(comment -> commentService.deleteCommentById(comment.getId()));

        // then
        StepVerifier.create(deleteComment).verifyComplete();
        StepVerifier.create(commentService.readCommentsByAuthorId("testAuthorId"))
                .expectNextCount(0)
                .verifyComplete();
    }


}