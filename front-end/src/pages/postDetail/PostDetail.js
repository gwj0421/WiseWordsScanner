import React, {useEffect, useState} from 'react';
import {Navigate, useNavigate, useParams} from 'react-router-dom';

import './PostDetail.css';
import {httpClientForCredentials} from "../../index";
import checkAuth from "../function/authUtils";


function PostDetail() {
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
    const [likeState, setLikeState] = useState(0);

    const fetchData = async () => {
        const response = await httpClientForCredentials.get(`/api/post/id/${postId}`);
        setTargetPost(response.data);
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
    useEffect(() => {
        checkAuth().then(initLike).then(fetchData);
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
            </div>
        </>
    );
}

export default PostDetail;