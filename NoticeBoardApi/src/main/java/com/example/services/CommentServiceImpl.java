package com.example.services;

import com.example.dao.Comment;
import com.example.dto.CommentForm;
import com.example.dto.CommentWithReplies;
import com.example.repository.CommentRepository;
import com.example.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final ReplyService replyService;
    private final FormConverter formConverter;
    private final DeleteService deleteService;

    @Override
    public Mono<Comment> createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Mono<String> createComment(ServerHttpRequest request, CommentForm commentForm) {
        String authorId = CookieUtils.getUserId(request);
        return formConverter.toComment(commentForm, authorId)
                .flatMap(commentRepository::save)
                .map(Comment::getId);
    }

    @Override
    public Mono<Comment> readCommentById(String id) {
        return commentRepository.findById(id);
    }

    @Override
    public Flux<Comment> readCommentsByAuthorId(String id) {
        return commentRepository.findCommentsByAuthorId(id);
    }

    @Override
    public Mono<List<CommentWithReplies>> readCommentsByPostId(String postId,String authorId) {
        return commentRepository.findCommentsByPostIdOrderByCreatedDate(postId)
                .concatMap(comment -> replyService.readReplyByCommentId(comment.getId(),authorId)
                        .collectList()
                        .map(replies -> new CommentWithReplies(comment,replies,authorId)))
                .collectList();
    }

    @Override
    public Mono<Void> deleteCommentById(String id) {
        return deleteService.deleteComment(id);
    }
}
