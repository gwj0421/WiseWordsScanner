import axios from 'axios';
import React, {useState, useEffect} from 'react';

import CommonTable from '../components/table/CommonTable';
import CommonTableColumn from '../components/table/CommonTableColumn';
import CommonTableRow from '../components/table/CommonTableRow';
import {Link, useLocation} from "react-router-dom";
import PostDetailFooter from "./postDetail/PostDetailFooter";
import {httpClientForCredentials} from "../index";
import checkAuth from "./function/checkAuth";
import checkAuthForUnknown from "./function/checkAuthByUnknown";

const PostsTable = ({setLoggedIn}) => {
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
        checkAuthForUnknown(setLoggedIn);
        fetchPosts();
    }, [currentPage, pageSize]); // 페이지 번호나 페이지 크기가 바뀔 때마다 이를 조정하여 호출할 수 있습니다.


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

    return (
        <div>
            <div>
                <CommonTable headersName={['글번호', '제목', '등록일', '작성자', '조회수','추천수']}>
                    {posts.map((post, index) => (
                        <CommonTableRow key={post.postId}>
                            <CommonTableColumn>{currentPage * pageSize + index + 1}</CommonTableColumn>
                            <CommonTableColumn>
                                <Link to={`/post/id/${post.postId}`} className="post-link">
                                    {post.title.length > 15 ? post.title.substring(0,16)+"...":post.title}
                                </Link>
                            </CommonTableColumn>
                            <CommonTableColumn>{post.createdDate}</CommonTableColumn>
                            <CommonTableColumn>{post.authorUserId}</CommonTableColumn>
                            <CommonTableColumn>{post.visitCnt}</CommonTableColumn>
                            <CommonTableColumn>{post.recommendCnt}</CommonTableColumn>
                        </CommonTableRow>
                    ))}
                </CommonTable>
                <PostDetailFooter
                    nextPage={nextPage}
                    prevPage={prevPage}
                    goToPage={goToPage}
                    totalPages={totalPages}
                ></PostDetailFooter>
            </div>
        </div>
    );
};

export default PostsTable;
