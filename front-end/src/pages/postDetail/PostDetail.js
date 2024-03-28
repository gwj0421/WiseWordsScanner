import React, {useEffect, useState} from 'react';
import {Navigate, useNavigate, useParams} from 'react-router-dom';

import './PostDetail.css';
import {httpClientForCredentials} from "../../index";
import checkAuth from "../function/checkAuth";


function PostDetail({setLoggedIn}) {
    const {postId} = useParams();
    const [targetPost, setTargetPost] = useState({
        title: 'Loading...',
        authorUserId: 'Loading...',
        recommendCnt: 0,
        unRecommendCnt: 0,
        createdDate: 'Loading...',
        content: 'Loading...',
        visitCnt: 0
    });
    const [commentsWithReplies, setCommentsWithReplies] = useState([]);
    const [likeState, setLikeState] = useState(0);
    const [authorUserId, setAuthorUserId] = useState('');

    const [newCommentContent, setNewCommentContent] = useState('');
    // const [newReplyContent, setNewReplyContent] = useState('');
    const [newReplyContents, setNewReplyContents] = useState({});
    const [replyTextareaVisible, setReplyTextareaVisible] = useState({});

    const fetchData = async () => {
        const response = await httpClientForCredentials.get(`/api/post/id/${postId}`);
        setTargetPost(response.data.post);
        setCommentsWithReplies(response.data.comments)
    };

    const initLike = async () => {
        const response = await httpClientForCredentials.get(`/api/reco/post/init/${postId}`);
        setLikeState(response.data);
    }
    const handleLike = async (action) => {
        if (action !== likeState) {
            const response = await httpClientForCredentials.get(`/api/reco/post/${postId}?isRecommend=${action === 1}`);
            if (response.status === 400) {
                return;
            }
            if (likeState === 0) {
                if (action === 1) {
                    targetPost.recommendCnt += 1;
                } else if (action === -1) {
                    targetPost.unRecommendCnt += 1;
                }
            } else {
                if (action === 1) {
                    targetPost.recommendCnt += 1;
                    targetPost.unRecommendCnt -= 1;
                } else {
                    targetPost.unRecommendCnt += 1;
                    targetPost.recommendCnt -= 1;
                }
            }

            // 좋아요 상태를 변경하고 다시 데이터를 불러옴
            setLikeState(action);
        }
    };

    // 댓글을 작성하는 함수
    const handleCommentSubmit = async () => {
        if (newCommentContent.length < 1) {
            alert("댓글의 최소 길이는 1 이상입니다.")
            return;
        }
        const response = await httpClientForCredentials.post(`/api/comment`, {
            postId: postId,
            content: newCommentContent
        }, {
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const newComment = {
            comment: response.data,
            replies: []
        }

        // 기존 댓글 배열에 새로운 댓글 추가
        setCommentsWithReplies(prevState => [...prevState, newComment]);

        // 댓글 작성 양식 초기화
        setNewCommentContent('');
    };

    const handleReplySubmit = async (commentId) => {
        if (newReplyContents[commentId].length < 1) {
            alert("답글의 최소 길이는 1 이상입니다.")
            return;
        }
        const response = await httpClientForCredentials.post(`/api/reply`, {
            commentId: commentId,
            replyContent: newReplyContents[commentId]
        }, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
        // 새로운 댓글 생성
        const newReply = {
            authorUserId: response.data.authorUserId,
            replyId: response.data.replyId,
            replyContent: response.data.replyContent
        };

        // 새로운 replies 리스트를 사용하여 상태를 업데이트
        const updatedCommentsWithReplies = commentsWithReplies.map(commentWithReplies => {
            if (commentWithReplies.comment.commentId === commentId) {
                // 현재 댓글의 replies를 업데이트된 리스트로 교체
                return {
                    ...commentWithReplies,
                    replies: [...commentWithReplies.replies, newReply]
                };
            }
            return commentWithReplies;
        });

        // 업데이트된 댓글과 답글을 포함한 상태를 설정
        setCommentsWithReplies(updatedCommentsWithReplies);
        // 답글 작성 양식 초기화
        setNewReplyContents(prevState => {
            const newState = {...prevState};
            delete newState[commentId]; // 해당 댓글에 대한 입력 상태를 초기화
            return newState;
        });
    };

    // 답글 입력 상태를 업데이트하는 함수
    const handleReplyContentChange = (commentId, value) => {
        setNewReplyContents(prevState => ({
            ...prevState,
            [commentId]: value // 해당 commentId에 대한 입력 상태 업데이트
        }));
    };

    const handleToggleReplyTextarea = (commentId) => {
        setReplyTextareaVisible(prevState => ({
            ...prevState,
            [commentId]: !prevState[commentId] // 해당 댓글의 상태를 토글
        }));
    };
    const changeInputValueHandler = (event: KeyboardEvent<HTMLInputElement>, commentId?: string) => {
        if (event.nativeEvent.isComposing) {
            return;
        }
        if (event.key === "Enter") {
            event.preventDefault();
            if (commentId) {
                handleReplySubmit(commentId);
            } else {
                handleCommentSubmit();
            }
        }
    };

    useEffect(() => {
        checkAuth(setLoggedIn).then(authResult => setAuthorUserId(authResult)).then(initLike).then(fetchData);
    }, []);

    return (
        <>
            <h2 align="center">게시글 상세정보</h2>
            <div className="post-detail-view-wrapper">
                <div className="post-detail-view-row">
                    <label>제목</label>
                    <label>{targetPost.title}</label>
                </div>
                <div className="post-detail-view-row">
                    <label>작성자</label>
                    <div>
                        {targetPost.authorUserId}
                    </div>
                </div>
                <div className="post-detail-view-row">
                    <label>추천 / 비추천 수</label>
                    <div>
                        <button onClick={() => handleLike(1)}
                                style={{backgroundColor: likeState === 1 ? 'green' : 'gray'}}>{targetPost.recommendCnt}</button>
                        <button onClick={() => handleLike(-1)}
                                style={{backgroundColor: likeState === -1 ? 'red' : 'gray'}}>{targetPost.unRecommendCnt}</button>
                    </div>
                </div>
                <div className="post-detail-view-row">
                    <label>조회수</label>
                    <label>{targetPost.visitCnt}</label>
                </div>
                <div className="post-detail-view-row">
                    <label>작성일</label>
                    <label>{targetPost.createdDate}</label>
                </div>
                <div className="post-detail-view-row">
                    <label>내용</label>
                    <div>
                        {targetPost.content}
                    </div>
                </div>
                <div className="textarea-area">
                    <textarea
                        className="textarea"
                        value={newCommentContent}
                        onChange={e => setNewCommentContent(e.target.value)}
                        onKeyDown={changeInputValueHandler}
                        rows="4"
                        cols="50"
                        maxLength={512}
                    />
                    <button className="textarea-button" onClick={handleCommentSubmit}>Submit</button>
                </div>
                {commentsWithReplies.map((commentWithReplies, index) => (
                    <div key={commentWithReplies.comment.commentId}>
                        <p onClick={() => handleToggleReplyTextarea(commentWithReplies.comment.commentId)}>
                            <strong>{commentWithReplies.comment.authorUserId}</strong> {commentWithReplies.comment.content}
                        </p>
                        {replyTextareaVisible[commentWithReplies.comment.commentId] &&
                            <div className="textarea-area">
                                    <textarea
                                        className="textarea"
                                        value={newReplyContents[commentWithReplies.comment.commentId] || ''}
                                        onChange={e => handleReplyContentChange(commentWithReplies.comment.commentId, e.target.value)}
                                        onKeyDown={(event) => changeInputValueHandler(event,commentWithReplies.comment.commentId)}
                                        rows="4"
                                        cols="50"
                                        maxLength={512}
                                    />
                                <button className="textarea-button" onClick={() => handleReplySubmit(commentWithReplies.comment.commentId)}>Submit
                                </button>
                            </div>
                        }
                        <div className="post-detail-view-row">
                            {/* 해당 댓글의 답글 출력 */}
                            <div className="reply-container">
                                {commentWithReplies.replies.map((reply, index) => (
                                    <div key={index}>
                                        <p><strong>{reply.authorUserId}</strong> {reply.replyContent}</p>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </>
    );
}

export default PostDetail;