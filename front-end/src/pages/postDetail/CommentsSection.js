import React from 'react';

const Comment = ({ comment }) => {
    return (
        <div className="comment">
            <div className="comment-author">{comment.author}</div>
            <div className="comment-content">{comment.content}</div>
        </div>
    );
};

const Reply = ({ reply }) => {
    return (
        <div className="reply">
            <div className="reply-author">{reply.author}</div>
            <div className="reply-content">{reply.content}</div>
        </div>
    );
};

const CommentWithReplies = ({ commentWithReplies }) => {
    const { comment, replies } = commentWithReplies;

    return (
        <div className="comment-with-replies">
            <Comment comment={comment} />
            <div className="replies">
                {replies.map((reply, index) => (
                    <Reply key={index} reply={reply} />
                ))}
            </div>
        </div>
    );
};

const CommentsSection = ({ commentsWithReplies }) => {
    return (
        <div className="comments-section">
            {commentsWithReplies.map((commentWithReplies, index) => (
                <CommentWithReplies key={index} commentWithReplies={commentWithReplies} />
            ))}
        </div>
    );
};

export default CommentsSection;