package com.example.services;

import com.example.dao.Comment;
import com.example.dao.Post;
import com.example.dao.Reply;
import com.example.dto.CommentForm;
import com.example.dto.PostForm;
import com.example.dto.ReplyForm;
import reactor.core.publisher.Mono;

public interface FormConverter {
    Mono<Post> toPost(PostForm postForm,String authorId);
    Mono<Comment> toComment(CommentForm commentForm,String authorId);
    Mono<Reply> toReply(ReplyForm replyForm,String authorId);
}
