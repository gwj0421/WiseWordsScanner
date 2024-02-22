package com.example;

import com.example.dao.Post;
import com.example.dao.SiteUser;
import com.example.dto.PageablePostsResponse;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import com.example.repository.ReplyRepository;
import com.example.repository.SiteUserRepository;
import com.example.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@Slf4j
class NoticeBoardApiApplicationTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private SiteUserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private PostService postService;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        commentRepository.deleteAll();
        replyRepository.deleteAll();
    }

    @Test
    void makeTestResource() {
        // given
        SiteUser savedUser1 = userRepository.save(new SiteUser("testUser1", "testUserId1", "testPassword1", "testEmail1@gmail.com")).block();

        // when
        Flux<Post> postFlux = Flux.range(1, 50).concatMap(index -> postRepository.save(new Post(savedUser1, "title " + index, "content " + index)));
        Mono<PageablePostsResponse> sendFront = postService.getPostsByPage(0, 10);

        // then
        StepVerifier.create(postFlux)
                .expectNextCount(50)
                .verifyComplete();

        StepVerifier.create(sendFront)
                .expectNextMatches(pageablePostsResponse -> pageablePostsResponse.getPosts().size() == 10)
                .verifyComplete();
    }
}