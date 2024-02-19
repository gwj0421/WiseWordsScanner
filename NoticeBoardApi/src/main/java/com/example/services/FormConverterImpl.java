package com.example.services;

import com.example.dao.Comment;
import com.example.dao.Post;
import com.example.dao.Reply;
import com.example.dto.CommentForm;
import com.example.dto.PostForm;
import com.example.dto.ReplyForm;
import com.example.repository.CommentRepository;
import com.example.repository.PostRepository;
import com.example.repository.SiteUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormConverterImpl implements FormConverter {
    private final SiteUserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    public Mono<Post> toPost(PostForm postForm) {
        return userRepository.findById(postForm.getAuthorId())
                .map(user -> new Post(user, postForm.getContent()));
    }

    @Override
    public Mono<Comment> toComment(CommentForm commentForm) {
        return userRepository.findById(commentForm.getAuthorId())
                .flatMap(user -> postRepository.findPostById(commentForm.getPostId())
                        .map(post -> new Comment(user, post, commentForm.getContent())));
    }

    @Override
    public Mono<Reply> toReply(ReplyForm replyForm) {
        return userRepository.findById(replyForm.getAuthorId())
                .flatMap(user -> commentRepository.findById(replyForm.getCommentId())
                        .map(comment -> new Reply(user, comment, replyForm.getReplyContent())));
    }
}
