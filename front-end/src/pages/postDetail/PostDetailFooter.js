import React, {useEffect, useState} from 'react';
import {Link, Navigate, useNavigate} from 'react-router-dom';
import './PostDetailFooter.css';

const PostDetailFooter = ({ nextPage, prevPage, goToPage, totalPages }) => {
    // const {headersName, children} = props;
    const [keyword, setKeyword] = useState('');
    const navigate = useNavigate();
    const handleSearch = () => {
        if (keyword.length >= 3) {
            navigate(`/search/${keyword}`);
        }
        console.log("error : add more keyword");
    };

    return (<div className="post-detail-footer">
        <div className="post-detail-footer-navi">
            <button onClick={() => prevPage()}>Previous</button>
            {Array.from({length: totalPages}, (_, index) => (
                <button key={index} onClick={() => goToPage(index)}>
                    {index + 1}
                </button>
            ))}
            <button onClick={() => nextPage()}>
                Next
            </button>
        </div>
        <div className="post-detail-footer-search">
            <input type="text" value={keyword} onChange={(event) => setKeyword(event.target.value)}/>
            <button className="post-detail-view-go-list-btn" onClick={handleSearch}>
                검색
            </button>
        </div>
    </div>);
};

export default PostDetailFooter;