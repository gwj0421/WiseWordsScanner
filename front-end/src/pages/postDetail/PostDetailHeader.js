import React, {useEffect, useState} from 'react';
import {Link, Navigate, useNavigate} from 'react-router-dom';
import './PostDetailHeader.css';

const PostDetailHeader = props => {
    const {headersName, children} = props;
    const [keyword, setKeyword] = useState('');
    const navigate = useNavigate();

    const handleSearch = () => {
        if (keyword.length >= 3) {
            navigate(`/search/${keyword}`);
        }
        console.log("error : add more keyword");
    };

    return (<div className="post-detail-header">
        {/*<h2 align="left">게시판</h2>*/}
        <Link to='/post'>
            <button align="right" className="post-detail-view-go-list-btn">
                게시글 작성
            </button>
        </Link>
        <input type="text" value={keyword} onChange={(event) => setKeyword(event.target.value)}/>
        <button align="right" className="post-detail-view-go-list-btn" onClick={handleSearch}>
            검색
        </button>
    </div>);
};

export default PostDetailHeader;