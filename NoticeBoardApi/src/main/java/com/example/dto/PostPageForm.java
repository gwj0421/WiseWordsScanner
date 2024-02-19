package com.example.dto;

import com.example.dao.Comment;
import com.example.dao.Post;
import com.example.dao.Reply;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PostPageForm {
    private Post post;
    private List<CommentWithReplies> comments;

    public PostPageForm(Post post, List<CommentWithReplies> comments) {
        this.post = post;
        this.comments = comments;
    }

    @Getter @Setter
    public static class CommentWithReplies {
        private Comment comment;
        private List<Reply> replies;

        public CommentWithReplies(Comment comment, List<Reply> replies) {
            this.comment = comment;
            this.replies = replies;
        }
    }
}
