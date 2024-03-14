import axios from 'axios';
import React, {useState, useEffect} from 'react';

import CommonTable from '../components/table/CommonTable';
import CommonTableColumn from '../components/table/CommonTableColumn';
import CommonTableRow from '../components/table/CommonTableRow';
import {Link, useLocation} from "react-router-dom";
import PostDetailHeader from "./postDetail/PostDetailHeader";
import {httpClientForCredentials} from "../index";

const PostsTable = () => {
    const [posts, setPosts] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [pageSize, setPageSize] = useState(10);

    const fetchPosts = async () => {
        try {
            const response = await httpClientForCredentials.get(`/api/post/page?page=${currentPage}&size=${pageSize}`, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            const data = response.data;

            setPosts(data.posts);
            setTotalPages(data.totalPages);
        } catch (error) {
            console.error('fail : ', error);
        }
    };


    useEffect(() => {
        fetchPosts();
    }, [currentPage,pageSize]); // 페이지 번호나 페이지 크기가 바뀔 때마다 이를 조정하여 호출할 수 있습니다.


    const goToPage = (pageNumber) => {
        setCurrentPage(pageNumber);
    }

    const nextPage = () => {
        if (currentPage < totalPages) {
            setCurrentPage(currentPage + 1);
        }
    };
    const prevPage = () => {
        if (currentPage > 0) {
            setCurrentPage(currentPage - 1);
        }
    };

    const logout = async () => {
        await httpClientForCredentials.get(`/api/user/logout`, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    };

    return (
        <div>
            <div>
                <PostDetailHeader></PostDetailHeader>
                <CommonTable headersName={['글번호', '제목', '등록일', '작성자', '추천수']}>
                    {posts.map((post, index) => (
                        <CommonTableRow key={post.postId}>
                            <CommonTableColumn>{currentPage * pageSize + index + 1}</CommonTableColumn>
                            <CommonTableColumn>
                                <Link to={`/post/id/${post.postId}`}>
                                    {post.title}
                                </Link>
                            </CommonTableColumn>
                            <CommonTableColumn>{post.createdDate}</CommonTableColumn>
                            <CommonTableColumn>{post.authorUserId}</CommonTableColumn>
                            <CommonTableColumn>{post.recommendCnt}</CommonTableColumn>
                        </CommonTableRow>
                    ))}
                </CommonTable>
            </div>
            <button onClick={() => prevPage()}>Previous</button>
            {Array.from({length: totalPages}, (_, index) => (
                <button key={index} onClick={() => goToPage(index)}>
                    {index + 1}
                </button>
            ))}
            <button onClick={() => nextPage()}>
                Next
            </button>
            <Link to={'/login'}>
                Login
            </Link>
            <button onClick={() => logout()}>
                Logout
            </button>
        </div>
    );
};

export default PostsTable;
