package com.example.services;

import com.example.dao.Reply;
import com.example.dto.PostPageForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PageServiceImpl implements PageService {
    private final PostService postService;
    private final CommentService commentService;
    private final ReplyService replyService;

    @Override
    public Mono<PostPageForm> getPostPageByPostId(String postId) {
        return postService.readPostById(postId)
                .flatMap(post -> {
                    Mono<List<PostPageForm.CommentWithReplies>> commentsWithRepliesMono = commentService.readCommentsByPostId(postId)
                            .flatMap(comment -> {
                                Mono<List<Reply>> repliesMono = replyService.readReplyByCommentId(comment.getId()).collectList();
                                return Mono.zip(Mono.just(comment), repliesMono);
                            })
                            .collectList()
                            .map(commentWithRepliesList -> commentWithRepliesList.stream()
                                    .map(commentWithReplies -> new PostPageForm.CommentWithReplies(commentWithReplies.getT1(), commentWithReplies.getT2()))
                                    .toList());

                    return commentsWithRepliesMono.map(commentsWithReplies -> new PostPageForm(post, commentsWithReplies));
                })
                .switchIfEmpty(Mono.defer(Mono::empty));
    }
}
