import React, {useEffect, useState} from 'react';
import {Navigate, useNavigate, useParams} from 'react-router-dom';

import './PostDetail.css';
import {httpClientForCredentials} from "../../index";
import checkAuth from "../function/checkAuth";


function PostDetail({setLoggedIn}) {
    const {postId} = useParams();
    const [userIdInUse, setUserIdInUse] = useState('');
    const [post, setPost] = useState({
        title: 'Loading...',
        authorUserId: 'Loading...',
        recommendCnt: 0,
        notRecommendCnt: 0,
        createdDate: 'Loading...',
        content: 'Loading...',
        visitCnt: 0,
        recommendCode: 0
    });
    const [commentsWithReplies, setCommentsWithReplies] = useState(new Map());
    const [newCommentContent, setNewCommentContent] = useState('');
    const [newReplyContents, setNewReplyContents] = useState({});

    const testData = () => {
        const commentWithRepliesMap = new Map();
        for (let i = 0; i < 5; i++) {
            // ['userId', 'test reply user Id'], ['content', 'test reply content'], ['recommendCnt', 88], ['recommend', false]
            const replies = new Map();
            for (let j = 0; j < 3; j++) {
                replies.set('test comment Id '+i+j,{
                    'userId': 'test reply user Id '+i+j,
                    'content': 'test reply content '+i+j,
                    'recommendCnt': 88,
                    'recommend': false
                })
            }
            commentWithRepliesMap.set('test comment id '+i, {
                'userId': 'test user Id '+i,
                'content': 'test comment content '+i,
                'recommendCnt': 77,
                'recommend': false,
                'visible': false,
                'replies': replies})
        }
        return commentWithRepliesMap;
    };
    const testFetchData = () => {
        setCommentsWithReplies(testData());
    };
    const fetchData = async () => {
        const response = await httpClientForCredentials.get(`/api/post/id/${postId}`);
        setPost(response.data.post);
        const commentWithRepliesMap = new Map();
        response.data.comments.forEach(commentObj => {
            const repliesMap = new Map();
            commentObj.replies.forEach(replyObj => {
                repliesMap.set(replyObj.replyId, {
                    userId: replyObj.authorUserId,
                    content: replyObj.replyContent,
                    recommendCnt: replyObj.recommendCnt,
                    recommend: replyObj.recommend
                });
            })
            // key = commentId , value = 작자 아이디, 댓글 내용, 추천수, 추천유무, 댓글들, visible유무
            commentWithRepliesMap.set(commentObj.comment.commentId, {
                userId: commentObj.comment.authorUserId,
                content: commentObj.comment.content,
                recommendCnt: commentObj.comment.recommendCnt,
                recommend: commentObj.comment.recommend,
                replies: repliesMap,
                visible: false
            });

        })
        setCommentsWithReplies(commentWithRepliesMap);
    };

    const handlePostLike = async (action) => {
        let nextRecommendCnt = post.recommendCnt;
        let nextNotRecommendCnt = post.notRecommendCnt;
        if (action !== post.recommendCode) {
            if (post.recommendCode === 0) {
                if (action === 1) {
                    nextRecommendCnt += 1;
                } else {
                    nextNotRecommendCnt += 1;
                }
            } else {
                if (action === 1) {
                    nextRecommendCnt += 1;
                    nextNotRecommendCnt -= 1;
                } else {
                    nextNotRecommendCnt += 1;
                    nextRecommendCnt -= 1;
                }
            }
            setPost(prevState => ({
                ...prevState,
                recommendCnt: nextRecommendCnt, notRecommendCnt: nextNotRecommendCnt, recommendCode: action
            }));
            await httpClientForCredentials.get(`/api/reco/post/${postId}?isRecommend=${action === 1}`);
        } else {
            if (action === 1) {
                nextRecommendCnt -= 1;
            } else if (action === -1) {
                nextNotRecommendCnt -= 1;
            }
            setPost(prevState => ({
                ...prevState,
                recommendCnt: nextRecommendCnt, notRecommendCnt: nextNotRecommendCnt, recommendCode: 0
            }));
            await httpClientForCredentials.delete(`/api/reco/post/${postId}?isRecommend=${action === 1}`);
        }
    };

    const handleCommentLike = async (commentId, action) => {
        if (action ^ commentsWithReplies.get(commentId).recommend) {
            setCommentsWithReplies(prevState => {
                const newCommentsWithReplies = new Map(prevState);

                // 1. 해당하는 댓글의 답글 맵을 찾습니다.
                const targetComment = newCommentsWithReplies.get(commentId);

                if (action) {
                    console.log(1);
                    targetComment.recommendCnt++;
                    // await httpClientForCredentials.get(`/api/reco/comment/${commentId}?isRecommend=${action}`);
                } else {
                    console.log(2);
                    targetComment.recommendCnt--;
                    // await httpClientForCredentials.delete(`/api/reco/comment/${commentId}?isRecommend=${action}`);
                }
                targetComment.recommend = !targetComment.recommend;
                newCommentsWithReplies.set(commentId, targetComment);
                return newCommentsWithReplies;
            })
            if (action) {
                await httpClientForCredentials.get(`/api/reco/comment/${commentId}?isRecommend=${action}`);
            } else {
                await httpClientForCredentials.delete(`/api/reco/comment/${commentId}?isRecommend=${action}`);
            }
        }
        // setCommentsWithReplies(prevCommentsWithReplies => {
        //     const newCommentsWithReplies = new Map(prevCommentsWithReplies);
        //
        //     // 1. 해당하는 댓글의 답글 맵을 찾습니다.
        //     const targetComment = newCommentsWithReplies.get(commentId);
        //
        //     console.log("!");
        //     // 3. 새로운 답글을 추가합니다.
        //     if (action ^ targetComment.recommend) {
        //         console.log(action);
        //         console.log(targetComment.recommend);
        //         if (action) {
        //             console.log(1);
        //             targetComment.recommendCnt++;
        //             // await httpClientForCredentials.get(`/api/reco/comment/${commentId}?isRecommend=${action}`);
        //         } else {
        //             console.log(1);
        //             targetComment.recommendCnt--;
        //             // await httpClientForCredentials.delete(`/api/reco/comment/${commentId}?isRecommend=${action}`);
        //         }
        //         targetComment.recommend = !targetComment.recommend;
        //         newCommentsWithReplies.set(commentId, targetComment);
        //     }
        //     return newCommentsWithReplies;
        // });

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

        const nextMap = new Map(commentsWithReplies);
        nextMap.set(response.data, {
            userId: userIdInUse,
            content: newCommentContent,
            recommendCnt: 0,
            recommend: false,
            replies: new Map(),
            visible: false
        });
        // 기존 댓글 배열에 새로운 댓글 추가
        setCommentsWithReplies(nextMap);
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
        setCommentsWithReplies(prevCommentsWithReplies => {
            const newCommentsWithReplies = new Map(prevCommentsWithReplies);

            // 1. 해당하는 댓글의 답글 맵을 찾습니다.
            const targetComment = newCommentsWithReplies.get(commentId);

            // 2. 답글이 없는 경우 새로운 Map을 생성합니다.
            const updatedRepliesMap = targetComment ? new Map(targetComment.replies) : new Map();

            // 3. 새로운 답글을 추가합니다.
            updatedRepliesMap.set(response.data, {
                userId: userIdInUse,
                content: newReplyContents[commentId],
                recommendCnt: 0,
                recommend: false
            });
            // 4. 댓글과 답글을 포함한 상태를 업데이트합니다.
            newCommentsWithReplies.set(commentId, {
                ...prevCommentsWithReplies.get(commentId),
                replies: updatedRepliesMap
            });
            setNewReplyContents(prevState => {
                const newState = {...prevState};
                delete newState[commentId]; // 해당 댓글에 대한 입력 상태를 초기화
                return newState;
            });
            return newCommentsWithReplies;
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
        setCommentsWithReplies(prevMap => {
            // 기존의 Map을 복사하여 새로운 Map을 생성합니다.
            const newMap = new Map(prevMap);

            // commentId에 해당하는 객체를 가져옵니다.
            const comment = newMap.get(commentId);

            // comment 객체가 존재하면 recommend 값을 반대로 변경합니다.
            if (comment) {
                // recommend 값을 반대로 변경합니다.
                comment.visible = !comment.visible;

                // 변경된 객체를 다시 Map에 설정합니다.
                newMap.set(commentId, comment);
            }

            return newMap;
        });
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
        checkAuth(setLoggedIn).then(userId => setUserIdInUse(userId)).then(fetchData());
        // testFetchData();
    }, []);

    return (
        <>
            <div className="post-detail-header">
                <div className="post-detail-header-title">{post.title}</div>
                <div className="post-detail-header-meta-status">
                    <span className="post-detail-header-meta-status-left">
                        <span> {post.authorUserId}</span>
                    </span>
                    <span className="post-detail-header-meta-status-right">
                        <span>추천 </span>
                        <span className="post-detail-header-meta-status-right-recommend-cnt">{post.recommendCnt}</span>
                        <span> | 비추천 </span>
                        <span
                            className="post-detail-header-meta-status-right-not-recommend-cnt">{post.notRecommendCnt}</span>
                        <span
                            className="post-detail-header-meta-status-right"> | 댓글 {commentsWithReplies.size} | 조회수 {post.visitCnt} | 작성일 {post.createdDate} </span>
                    </span>

                </div>
            </div>
            <div className="post-detail-view-wrapper">
                <div className="post-detail-view-row">
                    <div>
                        {post.content}
                    </div>
                </div>
                <div className="post-detail-view-vote-area">
                    <button onClick={() => handlePostLike(1)}
                            className="post-detail-view-recommend-btn"
                            style={{backgroundColor: post.recommendCode === 1 ? 'green' : '#222222'}}>추천 {post.recommendCnt}
                    </button>
                    <button onClick={() => handlePostLike(-1)}
                            className="post-detail-view-recommend-btn"
                            style={{backgroundColor: post.recommendCode === -1 ? 'red' : '#222222'}}>비추천 {post.notRecommendCnt}</button>
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
                    <button className="textarea-button" onClick={handleCommentSubmit}>작성</button>
                </div>
                {[...commentsWithReplies.entries()].map(([key, value]) => (
                    <div key={key}>
                        <div className="comment-container">
                            <div className="comment-header">
                                <span className="comment-header-author">{value.userId}</span>
                            </div>
                            <div className="comment-content" onClick={() => handleToggleReplyTextarea(key)}>
                                <span>{value.content}</span>
                            </div>
                            {value.visible &&
                                <div className="textarea-area">
                                    <textarea
                                        className="textarea"
                                        value={newReplyContents[key] || ''}
                                        onChange={e => handleReplyContentChange(key, e.target.value)}
                                        onKeyDown={(event) => changeInputValueHandler(event, key)}
                                        rows="4"
                                        cols="50"
                                        maxLength={512}
                                    />
                                    <button className="textarea-button"
                                            onClick={() => handleReplySubmit(key)}>Submit
                                    </button>
                                </div>
                            }
                            {[...value.replies.entries()].map(([key, value]) => (
                                <div key={key}>
                                    <div className="reply-container">
                                        <div className="reply-header">
                                            <span className="reply-header-author">{value.userId}</span>
                                        </div>
                                        <div className="reply-content">
                                            <span>{value.content}</span>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>


                        {/*    <div className="reply-container">*/}
                        {/*        <div className="post-detail-header-meta-status">*/}
                        {/*<span className="post-detail-header-meta-status-left">*/}
                        {/*    <span> {post.authorUserId}</span>*/}
                        {/*</span>*/}
                        {/*        </div>*/}
                        {/*        <p className="comment"*/}
                        {/*           onClick={() => handleToggleReplyTextarea(key)}>*/}
                        {/*            <strong>{value.userId}</strong> {value.content}*/}
                        {/*        </p>*/}
                    {/*        /!*<button*!/*/}
                    {/*        /!*    style={{backgroundColor: value.recommend ? 'green' : '#222222'}}*!/*/}
                    {/*        /!*    onClick={() => handleCommentLike(key, !value.recommend)}>{value.recommendCnt}</button>*!/*/}
                    {/*    </div>*/}

                    {/*    {value.visible &&*/}
                    {/*        <div className="textarea-area">*/}
                    {/*                <textarea*/}
                    {/*                    className="textarea"*/}
                    {/*                    value={newReplyContents[key] || ''}*/}
                    {/*                    onChange={e => handleReplyContentChange(key, e.target.value)}*/}
                    {/*                    onKeyDown={(event) => changeInputValueHandler(event, key)}*/}
                    {/*                    rows="4"*/}
                    {/*                    cols="50"*/}
                    {/*                    maxLength={512}*/}
                    {/*                />*/}
                    {/*            <button className="textarea-button"*/}
                    {/*                    onClick={() => handleReplySubmit(key)}>Submit*/}
                    {/*            </button>*/}
                    {/*        </div>*/}
                    {/*    }*/}
                    {/*    <div className="post-detail-view-row">*/}
                    {/*        /!* 해당 댓글의 답글 출력 *!/*/}
                    {/*        <div className="reply-container">*/}
                    {/*            {[...value.replies.entries()].map(([key, value]) => (*/}
                    {/*                <div key={key} className="comment-reply-form">*/}
                    {/*                    <p><strong>{value.userId}</strong> {value.content}</p>*/}
                    {/*                    <button*/}
                    {/*                        style={{backgroundColor: value.recommend ? 'green' : '#222222'}}>{value.recommendCnt}</button>*/}
                    {/*                </div>*/}
                    {/*            ))}*/}
                    {/*        </div>*/}
                    {/*    </div>*/}
                    </div>
                ))}
            </div>
        </>
    );
}

export default PostDetail;