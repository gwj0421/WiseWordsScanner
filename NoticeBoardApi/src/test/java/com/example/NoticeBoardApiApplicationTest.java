package com.example;

import com.example.dao.Comment;
import com.example.dao.Post;
import com.example.dto.PageablePostsResponse;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import com.example.repository.ReplyRepository;
import com.example.services.PostService;
import com.example.services.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class NoticeBoardApiApplicationTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReplyRepository replyRepository;

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
        commentRepository.deleteAll();
        replyRepository.deleteAll();
        String authorId = "65f2ac1e289f4c18401edc8f";
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

    @Test
    void test(@Autowired RecommendService recommendService) {
        Comment comment = postRepository.save(new Post("testAuthorId", "testAuthorUserId", "testTitle", "testContent"))
                .flatMap(post -> commentRepository.save(new Comment(post, "testCommentAuthorId", "testCommentAuthorUserId", "testCommentContent")))
                .block();

        assertThat(commentRepository.findById(comment.getId()).block()).isNotNull();
        assertThat(commentRepository.findById(comment.getId()).block().getPost()).isNotNull();
        assertThat(commentRepository.findById(comment.getId()).block().getRecommend()).isEmpty();

        recommendService.recommendComment(comment.getId(), "testRecommenderUserId").block();
        assertThat(commentRepository.findById(comment.getId()).block()).isNotNull();
        assertThat(commentRepository.findById(comment.getId()).block().getPost()).isNotNull();
        assertThat(commentRepository.findById(comment.getId()).block().getRecommend()).hasSize(1);

    }
}