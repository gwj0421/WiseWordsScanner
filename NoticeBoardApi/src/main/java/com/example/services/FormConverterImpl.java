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
    public Mono<Post> toPost(PostForm postForm) {
        return Mono.empty();
//        return loadBalancedWebClientBuilder.build().get().uri("http://user-manage-api/id/" + postForm.getAuthorId())
//                .retrieve()
//                .bodyToMono(SiteUser.class)
//                .map(user -> new Post(user, postForm.getTitle(), postForm.getContent()));
    }

    @Override
    public Mono<Comment> toComment(CommentForm commentForm) {
        return Mono.empty();
//        return loadBalancedWebClientBuilder.build().get().uri("http://user-manage-api/id/" + commentForm.getAuthorId())
//                .retrieve()
//                .bodyToMono(SiteUser.class)
//                .flatMap(user -> postRepository.findPostById(commentForm.getPostId())
//                        .map(post -> new Comment(user, post, commentForm.getContent())));
    }

    @Override
    public Mono<Reply> toReply(ReplyForm replyForm) {
        return Mono.empty();
//        return loadBalancedWebClientBuilder.build().get().uri("http://user-manage-api/id/" + replyForm.getAuthorId())
//                .retrieve()
//                .bodyToMono(SiteUser.class)
//                .flatMap(user -> commentRepository.findById(replyForm.getCommentId())
//                        .map(comment -> new Reply(user, comment, replyForm.getReplyContent())));
    }
}
