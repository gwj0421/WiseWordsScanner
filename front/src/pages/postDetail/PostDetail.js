import React, {useEffect, useState} from 'react';
import {useParams} from 'react-router-dom';

import './PostDetail.css';
import axios from "axios";

function GetData(postId) {
    const [postDetail, setPostDetail] = useState({});

    useEffect(() => {
        axios.get(`http://127.0.0.1:8080/post/id/${postId}`).then((response) => {
            setPostDetail(response.data);
        })
    }, []);

    const item = (<>
        <h2 align="center">게시글 상세정보</h2>
        <div className="post-detail-view-wrapper">
            <div className="post-detail-view-row">
                <label>제목</label>
                <label>{postDetail.title}</label>
            </div>
            <div className="post-detail-view-row">
                <label>작성자</label>
                <div>
                    {postDetail.authorName}
                </div>
            </div>
            <div className="post-detail-view-row">
                <label>추천 / 비추천 수</label>
                <div>{`${postDetail.recommendCnt} / ${postDetail.unRecommendCnt}`}</div>
            </div>
            <div className="post-detail-view-row">
                <label>작성일</label>
                <label>{postDetail.createdDate}</label>
            </div>
            <div className="post-detail-view-row">
                <label>내용</label>
                <div>
                    {
                        postDetail.content
                    }
                </div>
            </div>
        </div>
    </>)

    return item;
}

function PostDetail() {
    const {postId} = useParams();
    const item = GetData(postId);

    return (<>
        <div>
            {item}
        </div>
    </>);
}

export default PostDetail;