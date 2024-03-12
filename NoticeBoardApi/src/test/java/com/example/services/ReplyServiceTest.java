package com.example.services;

import com.example.config.ServiceConfig;
import com.example.config.WebClientConfig;
import com.example.dao.Comment;
import com.example.dao.Post;
import com.example.dao.Reply;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import com.example.repository.ReplyRepository;
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

import java.util.HashSet;
import java.util.Set;

@DataMongoTest
@Import({ServiceConfig.class, WebClientConfig.class})
@Slf4j
class ReplyServiceTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ReplyService replyService;

    @BeforeEach
    @AfterEach
    void setUpAndTearDown() {
        postRepository.deleteAll().block();
        commentRepository.deleteAll().block();
        replyRepository.deleteAll().block();
    }

    @Test
    void Should_saveReply_When_givenCommentInfo() {
        // given
        Post savedPost = postRepository.save(new Post("testAuthorId", "testAuthorUserId","title","testPostContent")).block();
        Comment savedComment = commentRepository.save(new Comment(savedPost,"testAuthorId","testAuthorUserId", "testCommentContent")).block();

        // when
        Mono<Reply> createReply = replyService.createReply(new Reply(savedComment,"testAuthorId","testAuthorUserId", "testReplyContent"));

        // then
        StepVerifier.create(createReply)
                .expectNextMatches(reply -> reply.getId() != null && reply.getReplyContent().equals("testReplyContent"))
                .verifyComplete();
    }

    @Test
    void Should_returnComments_When_givenCommentInfos() {
        // given
        Post savedPost = postRepository.save(new Post("testAuthorId", "testAuthorUserId","title","testPostContent")).block();
        Comment savedComment = commentRepository.save(new Comment(savedPost, "testAuthorId", "testAuthorUserId","testCommentContent")).block();

        // when
        Flux<Reply> createReply = Flux.range(1, 3).flatMap(index ->
                replyService.createReply(new Reply(savedComment,"testAuthorId","testAuthorUserId", "testReplyContent " + index)));
        Set<String> replies = new HashSet<>();
        StepVerifier.create(createReply)
                .thenConsumeWhile(reply -> {
                    replies.add(reply.getId());
                    return true;
                })
                .verifyComplete();
        Flux<Reply> findReply = replyService.readReplyByCommentId(savedComment.getId());
        StepVerifier.create(findReply)
                .thenConsumeWhile(reply -> replies.contains(reply.getId()))
                .verifyComplete();
    }

    @Test
    void Should_deleteReply_When_givenReplyId() {
        // given
        Post savedPost = postRepository.save(new Post("testAuthorId", "testAuthorUserId","title","testPostContent")).block();
        Comment savedComment = commentRepository.save(new Comment(savedPost,"testAuthorId", "testAuthorUserId", "testCommentContent")).block();

        // when
        Mono<Void> deleteReply = replyService.createReply(new Reply(savedComment,"testAuthorId","testAuthorUserId", "testReplyContent"))
                .flatMap(reply -> replyService.deleteReplyById(reply.getId()));
        // then
        StepVerifier.create(deleteReply)
                .verifyComplete();
        StepVerifier.create(replyService.readReplyByCommentId(savedComment.getId()))
                .expectNextCount(0)
                .verifyComplete();
    }
}