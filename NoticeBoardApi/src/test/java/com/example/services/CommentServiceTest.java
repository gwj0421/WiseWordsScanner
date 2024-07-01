package com.example.services;

import com.example.config.ServiceConfig;
import com.example.config.WebClientConfig;
import com.example.dao.Comment;
import com.example.dao.Post;
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
    @Autowired
    ReplyRepository replyRepository;

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
    void Should_returnComment_When_givenAuthorId() {
        // given
        String authorId = "testAuthorId";
//        Post savedPost = postRepository.save(new Post(authorId, "testAuthorUserId", "title", "testPostContent")).block();

        // when
//        Flux<Comment> commentFlux = Flux.range(1, 3)
//                .flatMap(index -> commentService.createComment(new Comment(savedPost, authorId, "testAuthorUserId", "content " + index)));
//        Flux<Comment> expectedCommentsByAuthorId = commentService.readCommentsByAuthorId(authorId);

        Flux<Comment> expectedCommentsByAuthorId = postRepository.save(new Post(authorId, "testAuthorUserId", "title", "testPostContent"))
                .flatMapMany(post -> Flux.range(1, 3)
                        .flatMap(index -> commentService.createComment(new Comment(post, authorId, "testAuthorUserId", "content " + index))))
                .thenMany(commentService.readCommentsByAuthorId(authorId));

        // then
        StepVerifier.create(expectedCommentsByAuthorId)
                .expectNextCount(3)
                .thenConsumeWhile(comment -> comment.getAuthorId().equals(authorId) && comment.getContent().startsWith("content "))
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

//    @Test
//    void Should_readComments_When_givenPostId() {
//        // given
//        Post savedPost = postRepository.save(new Post("testAuthorId", "testAuthorUserId","title","testPostContent")).block();
//        Comment savedComment = commentRepository.save(new Comment(savedPost, "testAuthorId", "testAuthorUserId", "testCommentContent1")).block();
//        replyRepository.save(new Reply(savedComment, "testAuthorId", "testAuthorUserId", "testReplyContent1")).block();
//        replyRepository.save(new Reply(savedComment, "testAuthorId", "testAuthorUserId", "testReplyContent2")).block();
//        replyRepository.save(new Reply(savedComment, "testAuthorId", "testAuthorUserId", "testReplyContent3")).block();
//
//        // when
//        Mono<List<CommentWithReplies>> commentsWithRepliesByPostId = commentService.readCommentsByPostId(savedPost.getId());
//
//        // then
//        StepVerifier.create(commentsWithRepliesByPostId)
//                .expectNextMatches(commentWithReplies -> commentWithReplies.size() == 1 && commentWithReplies.get(0).getReplies().size()==3)
//                .verifyComplete();
//    }
}