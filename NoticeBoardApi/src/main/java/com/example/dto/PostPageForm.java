package com.example.dto;

import com.example.dao.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class PostPageForm {
    private PostForm post;
    private List<CommentWithReplies> comments;

    public PostPageForm(Post post, List<CommentWithReplies> comments, String authorId) {
        this.post = PostForm.getPostFormToShowDetail(post,authorId);
        this.comments = comments;
    }
}
