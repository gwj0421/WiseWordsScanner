import React, {useEffect, useState} from 'react';
import {httpClientForCredentials} from "../../index";
import {Navigate, useNavigate} from "react-router-dom";

import './MakingPost.css';

// function GetCategory() {
//     const [category, setCategory] = useState({});
//
//     useEffect(() => {
//         axios.get('http://127.0.0.1:8080/toyseven/voc/category').then((response)=> {
//             setCategory(response.data);
//         })
//     }, []);
//
//     const categories = (Object.values(category)).map((item) => (
//         <option key={item.id} value={item.id}>
//             {item.displayName}
//         </option>
//     ));
//
//     return categories;
// }

// const HandleQuestionSubmit = async ({body}) => {
//
//     const response = await axios.post(`http://127.0.0.1:8000/api/post/`, body, {
//         withCredentials: true, headers: {
//             'Content-Type': 'application/json'
//         }
//     })
//         .then((response) => {
//             console.log('status : ' + response.status);
//             window.location.replace(`/post/id/${response.data.id}`);
//         }).catch((error) => {
//             console.log('error : ' + error);
//         });
// }
//
// async function MakingPost() {
//     // const categories = GetCategory();
//     const response = await axios.get(`http://127.0.0.1:8000/api/user/authorization`, {
//         withCredentials: true
//     });
//     console.log(response.data);
//     if (response.data) {
//         console.log("gwj : page is not authorization");
//     } else {
//         console.log("gwj : success");
//     }
//     const [title, setTitle] = useState('');
//     const [content, setContent] = useState('');
//
//     const body = {
//         title: title, content: content
//     }
//
//     return (<>
//         <h2 align="center">게시글 작성</h2>
//         <div className="voc-view-wrapper">
//             <div className="voc-view-row">
//                 <label>제목</label>
//                 <input onChange={(event) => setTitle(event.target.value)}></input>
//             </div>
//             <div className="voc-view-row">
//                 <label>내용</label>
//                 <textarea onChange={(event) => setContent(event.target.value)}></textarea>
//             </div>
//             <button className="voc-view-go-list-btn" onClick={() => HandleQuestionSubmit({body})}>등록</button>
//         </div>
//     </>);
// }

const HandleQuestionSubmit = async (body,navigate) => {
    try {
        const response = await httpClientForCredentials.post('/api/post',body,{
            headers: {
                'Content-Type': 'application/json'
            }
        });
        navigate(`/post/id/${response.data.id}`);
    } catch (error) {
        console.log('error : ' + error);
    }
};

function MakingPost() {
    const navigate = useNavigate();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');

    const body = {
        title: title,
        content: content
    };

    useEffect(() => {
        if (httpClientForCredentials.defaults.headers.common['Authorization'] != undefined) {
            const fetchData = async () => {
                try {
                    const response = await httpClientForCredentials.get('/api/user/auth');
                    if (!response.data) {
                        navigate('/login', {replace: true});
                    }
                } catch (error) {
                    console.log('error : ' + error);
                }
            };

            fetchData();
        }
    }, []); // Empty dependency array means this effect will only run once when the component mounts

    if (httpClientForCredentials.defaults.headers.common['Authorization'] == undefined) {
        return <Navigate to={'/login'}/>;
    }

    return (
        <>
            <h2 align="center">게시글 상세정보</h2>
            <div className="post-detail-view-wrapper">
                <div className="post-detail-view-row">
                    <label>제목</label>
                    <input onChange={(event) => setTitle(event.target.value)}></input>
                </div>
                <div className="post-detail-view-row">
                    <label>내용</label>
                    <textarea onChange={(event) => setContent(event.target.value)}></textarea>
                </div>
                <div>
                    <button onClick={() => HandleQuestionSubmit(body, navigate)}>등록</button>
                </div>
            </div>
        </>
    );
    // return (
    //     <>
    //         <h2 align="center">게시글 작성</h2>
    //         <div>
    //             <div>
    //                 <label>제목</label>
    //                 <input onChange={(event) => setTitle(event.target.value)}></input>
    //             </div>
    //             <div>
    //                 <label>내용</label>
    //                 <textarea onChange={(event) => setContent(event.target.value)}></textarea>
    //                 <button onClick={() => HandleQuestionSubmit(body)}>등록</button>
    //             </div>
    //         </div>
    //     </>
    // );
}

export default MakingPost;