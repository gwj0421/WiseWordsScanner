package com.example.services;

import com.example.dao.Comment;
import com.example.dto.CommentForm;
import com.example.dto.TargetType;
import com.example.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final TargetType COMMENT_TARGET_TYPE = TargetType.COMMENT;
    private final CommentRepository commentRepository;
    private final RecommendService recommendService;
    private final FormConverter formConverter;
    private final DeleteService deleteService;

    @Override
    public Mono<Comment> createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Mono<Comment> createComment(CommentForm commentForm) {
        return formConverter.toComment(commentForm)
                .flatMap(commentRepository::save);
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
    public Flux<Comment> readCommentsByPostId(String id) {
        return commentRepository.findCommentsByPostId(id);
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
