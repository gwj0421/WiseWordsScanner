package com.example.services;

import com.example.dao.Comment;
import com.example.dto.*;
import com.example.exception.error.AuthorizationException;
import com.example.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    public static final String UID_KEY = "Uid";
    private static final TargetType COMMENT_TARGET_TYPE = TargetType.COMMENT;
    private final CommentRepository commentRepository;
    private final ReplyService replyService;
    private final RecommendService recommendService;
    private final FormConverter formConverter;
    private final DeleteService deleteService;

    @Override
    public Mono<Comment> createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Mono<CommentForm> createComment(ServerHttpRequest request, CommentForm commentForm) {
        if (!request.getCookies().get(UID_KEY).isEmpty()) {
            String authorId = request.getCookies().get(UID_KEY).get(0).getValue();
            return formConverter.toComment(commentForm, authorId)
                    .flatMap(commentRepository::save)
                    .map(CommentForm::getCommentFormToShowDetail);
        }
        return Mono.error(new AuthorizationException("createComment"));
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
    public Mono<List<CommentWithReplies>> readCommentsByPostId(String postId) {
        return commentRepository.findCommentsByPostId(postId)
                .flatMap(comment -> replyService.readReplyByCommentId(comment.getId())
                        .map(ReplyForm::getReplyFormToShowDetail)
                        .collectList()
                        .map(replies -> new CommentWithReplies(comment,replies)))
                .collectList();
    }

    @Override
    public Mono<Void> recommend(String commentId, String recommenderId, boolean userRecommend) {
        return recommendService.recommend((ReactiveMongoRepository) commentRepository, COMMENT_TARGET_TYPE, commentId, recommenderId, userRecommend);
    }

    @Override
    public Mono<Void> deleteCommentById(String id) {
        return deleteService.deleteComment(id);
    }
}
