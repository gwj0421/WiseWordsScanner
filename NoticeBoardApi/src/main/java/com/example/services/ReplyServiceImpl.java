package com.example.services;

import com.example.dao.Reply;
import com.example.dto.ReplyForm;
import com.example.repository.ReplyRepository;
import com.example.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService {
    private final ReplyRepository replyRepository;
    private final FormConverter formConverter;
    private final DeleteService deleteService;

    @Override
    public Mono<Reply> createReply(Reply reply) {
        return replyRepository.save(reply);
    }

    @Override
    public Mono<String> createReply(ServerHttpRequest request, ReplyForm replyForm) {
        String authorId = CookieUtils.getUserId(request);
        return formConverter.toReply(replyForm, authorId)
                .flatMap(replyRepository::save)
                .map(Reply::getId);
    }

    @Override
    public Mono<Reply> readReplyById(String id) {
        return replyRepository.findById(id);
    }

    @Override
    public Flux<ReplyForm> readReplyByCommentId(String commentId,String authorId) {
        return replyRepository.findAllByCommentIdOrderByCreatedDate(commentId)
                .map(reply -> ReplyForm.getReplyFormToShowDetail(reply,authorId));
    }

    @Override
    public Mono<Void> deleteReplyById(String id) {
        return deleteService.deleteReply(id);
    }
}
