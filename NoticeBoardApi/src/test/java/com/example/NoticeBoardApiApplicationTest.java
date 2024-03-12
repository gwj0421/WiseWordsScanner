package com.example;

import com.example.dao.Post;
import com.example.dto.PageablePostsResponse;
import com.example.repository.PostRepository;
import com.example.services.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@SpringBootTest
class NoticeBoardApiApplicationTest {
    @Autowired
    private PostRepository postRepository;
//    @Autowired
//    private CommentRepository commentRepository;
//    @Autowired
//    private ReplyRepository replyRepository;
    @Autowired
    private PostService postService;

//    @BeforeEach
//    void setUp() {
//        postRepository.deleteAll();
//        commentRepository.deleteAll();
//        replyRepository.deleteAll();
//    }

//    @Test
//    void remove() {
//
//    }

    @Test
    void makeTestResource() {
        postRepository.deleteAll();
        String authorId = "65ed563132839a0b9a9fe3c7";
        Flux<Post> postFlux = Flux.range(1, 50).concatMap(index -> postRepository.save(new Post(authorId, "gwj0421", "testTitle " + index, "content " + index)));
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