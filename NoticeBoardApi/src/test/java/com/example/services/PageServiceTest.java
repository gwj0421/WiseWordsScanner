package com.example.services;

import com.example.config.ServiceConfig;
import com.example.dao.Comment;
import com.example.dao.Post;
import com.example.dao.Reply;
import com.example.dao.SiteUser;
import com.example.dto.PostPageForm;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import com.example.repository.ReplyRepository;
import com.example.repository.SiteUserRepository;
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
@Import(ServiceConfig.class)
@Slf4j
class PageServiceTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private SiteUserRepository userRepository;
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
        userRepository.deleteAll().block();
        commentRepository.deleteAll().block();
        replyRepository.deleteAll().block();
    }

    @Test
    void Should_returnPostPage_When_givenPostAndUserAndCommentsAndReplies() {
        // given
        SiteUser savedUser1 = userRepository.save(new SiteUser("testName1", "testUserId", "testPassword", "testEmail@gamil.com")).block();
        SiteUser savedUser2 = userRepository.save(new SiteUser("testName2", "testUserId", "testPassword", "testEmail@gamil.com")).block();
        SiteUser savedUser3 = userRepository.save(new SiteUser("testName3", "testUserId", "testPassword", "testEmail@gamil.com")).block();
        Post savedPost = postRepository.save(new Post(savedUser1, "testPostContent")).block();
        Comment savedComment1 = commentRepository.save(new Comment(savedUser2, savedPost, "testCommentContent")).block();
        Reply reply1_1 = replyRepository.save(new Reply(savedUser1, savedComment1, "testReplyContent1-1")).block();
        Reply reply1_2 = replyRepository.save(new Reply(savedUser3, savedComment1, "testReplyContent1-2")).block();
        Comment savedComment2 = commentRepository.save(new Comment(savedUser3, savedPost, "testCommentContent")).block();
        Reply reply2_1 = replyRepository.save(new Reply(savedUser1, savedComment2, "testReplyContent2-1")).block();
        Reply reply2_2 = replyRepository.save(new Reply(savedUser2, savedComment2, "testReplyContent2-2")).block();

        // when
        Mono<PostPageForm> createPostPage = pageService.getPostPageByPostId(savedPost.getId());

        // then
        StepVerifier.create(createPostPage)
                .expectNextMatches(postPageForm -> postPageForm.getPost().getId().equals(savedPost.getId()) && postPageForm.getAuthor().getId().equals(savedUser1.getId()))
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

    @Test
    void test(@Autowired SiteUserService userService) {
        Mono<Void> deleteUser = userRepository.save(new SiteUser("testName", "testUserId", "testPassword", "testEmail@gamil.com"))
                .flatMap(user -> postRepository.save(new Post(user, "content")).then(userService.deleteUser(user.getId())));
        StepVerifier.create(deleteUser)
                .verifyComplete();
        StepVerifier.create(postRepository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }
}