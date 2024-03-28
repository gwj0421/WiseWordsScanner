package com.example.services;

import com.example.dao.Reply;
import com.example.dto.ReplyForm;
import com.example.dto.TargetType;
import com.example.exception.error.AuthorizationException;
import com.example.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
    public static final String UID_KEY = "Uid";
    private static final TargetType REPLY_TARGET_TYPE = TargetType.REPLY;
    private final ReplyRepository replyRepository;
    private final RecommendService recommendService;
    private final FormConverter formConverter;
    private final DeleteService deleteService;

    @Override
    public Mono<Reply> createReply(Reply reply) {
        return replyRepository.save(reply);
    }

    @Override
    public Mono<ReplyForm> createReply(ServerHttpRequest request, ReplyForm replyForm) {
        if (!request.getCookies().get(UID_KEY).isEmpty()) {
            String authorId = request.getCookies().get(UID_KEY).get(0).getValue();
            return formConverter.toReply(replyForm, authorId)
                    .flatMap(replyRepository::save)
                    .map(ReplyForm::getReplyFormToShowDetail);
        }
        return Mono.error(new AuthorizationException("createReply"));
    }

    @Override
    public Mono<Reply> readReplyById(String id) {
        return replyRepository.findById(id);
    }

    @Override
    public Flux<Reply> readReplyByCommentId(String commentId) {
        return replyRepository.findAllByCommentIdOrderByCreatedDate(commentId);
    }

    @Override
    public Mono<Void> recommend(String replyId, String recommenderId, boolean userRecommend) {
        return recommendService.recommend((ReactiveMongoRepository) replyRepository, REPLY_TARGET_TYPE, replyId, recommenderId, userRecommend);
    }

    @Override
    public Mono<Void> deleteReplyById(String id) {
        return deleteService.deleteReply(id);
    }
}
