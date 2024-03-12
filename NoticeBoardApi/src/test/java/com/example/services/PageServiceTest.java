package com.example.services;

import com.example.config.ServiceConfig;
import com.example.config.WebClientConfig;
import com.example.dao.Comment;
import com.example.dao.Post;
import com.example.dao.Reply;
import com.example.dto.PostPageForm;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@Import({ServiceConfig.class, WebClientConfig.class})
@Slf4j
class PageServiceTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private PageService pageService;

    @BeforeEach
    @AfterEach
    void setUpAndTearDown() {
        postRepository.deleteAll().block();
        commentRepository.deleteAll().block();
        replyRepository.deleteAll().block();
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
        Mono<PostPageForm> createPostPage = pageService.getPostPageByPostId(savedPost.getId());

        // then
        StepVerifier.create(createPostPage)
                .expectNextMatches(postPageForm -> postPageForm.getPost().getId().equals(savedPost.getId()) && postPageForm.getPost().getAuthorId().equals("testAuthorId1"))
                .verifyComplete();
        StepVerifier.create(commentRepository.findCommentsByPostId(savedPost.getId()))
                .expectNextMatches(comment -> comment.getId().equals(savedComment1.getId()))
                .expectNextMatches(comment -> comment.getId().equals(savedComment2.getId()))
                .verifyComplete();
        StepVerifier.create(replyRepository.findRepliesByCommentId(savedComment1.getId()))
                .expectNextMatches(reply -> reply.getId().equals(reply1_1.getId()))
                .expectNextMatches(reply -> reply.getId().equals(reply1_2.getId()))
                .verifyComplete();
        StepVerifier.create(replyRepository.findRepliesByCommentId(savedComment2.getId()))
                .expectNextMatches(reply -> reply.getId().equals(reply2_1.getId()))
                .expectNextMatches(reply -> reply.getId().equals(reply2_2.getId()))
                .verifyComplete();
    }
}