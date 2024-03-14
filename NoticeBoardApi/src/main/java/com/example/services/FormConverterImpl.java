package com.example.services;

import com.example.dao.Comment;
import com.example.dao.Post;
import com.example.dao.Reply;
import com.example.dto.CommentForm;
import com.example.dto.PostForm;
import com.example.dto.ReplyForm;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormConverterImpl implements FormConverter {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final WebClient.Builder loadBalancedWebClientBuilder;

    @Override
    public Mono<Post> toPost(PostForm postForm, String authorId) {
        return loadBalancedWebClientBuilder.build().get().uri("http://user-manage-api/user/id/" + authorId)
                .retrieve()
                .bodyToMono(String.class)
                .map(authorUserId -> new Post(authorId, authorUserId, postForm.getTitle(), postForm.getContent()));
    }

    @Override
    public Mono<Comment> toComment(CommentForm commentForm) {
        return Mono.empty();
    }

    @Override
    public Mono<Reply> toReply(ReplyForm replyForm) {
        return Mono.empty();
    }
}
