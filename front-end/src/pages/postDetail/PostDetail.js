import React, {useEffect, useState} from 'react';
import {Navigate, useNavigate, useParams} from 'react-router-dom';

import './PostDetail.css';
import {httpClientForCredentials} from "../../index";


function PostDetail() {
    const {postId} = useParams();

    const [targetPost, setTargetPost] = useState({
        title: '',
        authorUserId: '',
        recommendCnt: 0,
        unRecommendCnt: 0,
        createdDate: '',
        content: ''
    });

    const preventClose = (e) => {
        // 2. 해당 함수 안에 새로운 함수를 생성하는데, 이때 이 함수는 자바스크립트의 이벤트를 감지하게된다.
        e.preventDefault();
        // 2-1. 특정 이벤트에 대한 사용자 에이전트 (브라우저)의 기본 동작이 실행되지 않도록 막는다.
        e.returnValue = '';
        // 2-2. e.preventDefault를 통해서 방지된 이벤트가 제대로 막혔는지 확인할 때 사용한다고 한다.
        // 2-3. 더 이상 쓰이지 않지만, chrome 설정상 필요하다고 하여 추가함.
        // 2-4. returnValue가 true일 경우 이벤트는 그대로 실행되고, false일 경우 실행되지 않는다고 한다.
    };


    useEffect(() => {
        if (httpClientForCredentials.defaults.headers.common['Authorization'] != undefined) {
            const fetchData = async () => {
                try {
                    const response = await httpClientForCredentials.get(`/api/post/id/${postId}`);
                    setTargetPost(response.data);
                } catch (error) {
                    console.error("Error fetch data: ", error);
                }
            };
            fetchData();
        }
    },[]);

    if (httpClientForCredentials.defaults.headers.common['Authorization'] == undefined) {
        return <Navigate to={'/login'}/>;
    }

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
                    <div>{`${targetPost.recommendCnt} / ${targetPost.unRecommendCnt}`}</div>
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