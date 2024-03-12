import React from 'react';
import {Link, Navigate} from 'react-router-dom';
import './PostDetailHeader.css';

const PostDetailHeader = props => {
    const { headersName, children } = props;

    return (
        <div className="post-detail-header">
            {/*<h2 align="left">게시판</h2>*/}
            <Link to='/post'>
                <button align="right" className="post-detail-view-go-list-btn" >
                    게시글 작성
                </button>
            </Link>
        </div>
    )
}

export default PostDetailHeader;