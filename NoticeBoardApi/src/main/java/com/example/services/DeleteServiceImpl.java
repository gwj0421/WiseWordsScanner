package com.example.services;

import com.example.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DeleteServiceImpl implements DeleteService {
    private final SiteUserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final RecommendationRepository recommendationRepository;

    @Override
    public Mono<Void> deleteSiteUser(String id) {
        return userRepository.deleteById(id)
                .then(postRepository.deletePostsByAuthorId(id))
                .then(commentRepository.deleteCommentsByAuthorId(id))
                .then(replyRepository.deleteRepliesByAuthorId(id));
    }

    @Override
    public Mono<Void> deletePost(String id) {
        return postRepository.deleteById(id)
                .then(commentRepository.deleteCommentsByPostId(id))
                .then(replyRepository.deleteRepliesByCommentPostId(id));
    }

    @Override
    public Mono<Void> deleteComment(String id) {
        return commentRepository.deleteById(id)
                .then(replyRepository.deleteRepliesByCommentId(id));
    }

    @Override
    public Mono<Void> deleteReply(String id) {
        return replyRepository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteRecommend(String id) {
        // TODO: 2024/02/19 need to add deleting function
        return recommendationRepository.deleteById(id);
    }
}
