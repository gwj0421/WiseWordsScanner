import React, {useEffect, useState} from 'react';
import {httpClientForCredentials} from "../../index";
import {useNavigate} from "react-router-dom";

import './MakingPost.css';
import checkAuth from "../function/checkAuth";

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

const HandleQuestionSubmit = async (body, navigate) => {
    if (body.title.length < 1 || body.content.length < 1) {
        alert("제목 혹은 내용의 최소 길이는 1 이상입니다.")
        return;
    }
    await httpClientForCredentials.post('/api/post', body, {
        headers: {
            'Content-Type': 'application/json'
        }
    }).then((response) => navigate(`/post/id/${response.data.postId}`, {replace: true}));
};

function MakingPost({setLoggedIn}) {
    const navigate = useNavigate();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const body = {
        title: title,
        content: content
    };
    const [images, setImages] = useState([]);
    const [previewImages, setPreviewImages] = useState([]);
    const [ocrResult, setOcrResult] = useState('');

    // 이미지가 변경될 때 호출되는 함수
    const handleImageChange = (event) => {
        const selectedImages = event.target.files;
        setImages(selectedImages);

        const previewImagesArray = [];
        for (let i = 0; i < selectedImages.length; i++) {
            const imageURL = URL.createObjectURL(selectedImages[i]);
            previewImagesArray.push(imageURL);
        }
        setPreviewImages(previewImagesArray);
    };

    // 이미지 업로드 요청을 보내는 함수
    const handleImageUpload = async () => {
        if (images.length === 0) {
            console.error('No images selected');
            return;
        }

        const formData = new FormData();
        // formData.append('images', images);
        for (let i = 0; i < images.length; i++) {
            formData.append('images', images[i]);
        }
        const response = await httpClientForCredentials.post('/api/ocr', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
        setOcrResult(response.data);
    };

    useEffect(() => {
        checkAuth(setLoggedIn);
    },[]);

    return (
        <>
            <h2 align="center">게시글 상세정보</h2>
            <div className="post-detail-view-wrapper">
                <div className="post-detail-view-row">
                    <label>제목</label>
                    <input onChange={(event) => setTitle(event.target.value)} maxLength={16}></input>
                </div>
                <div className="post-detail-view-row">
                    <label>내용</label>
                    <textarea onChange={(event) => setContent(event.target.value)} maxLength={512}></textarea>
                </div>
                <div className="post-detail-view-row">
                    <label>이미지 추출 내용</label>
                    <textarea value={ocrResult} onChange={(event) => setOcrResult(event.target.value)}></textarea>
                </div>
                <div>
                    <input type="file" multiple onChange={handleImageChange} />
                    {previewImages.map((imageURL, index) => (
                        <img key={index} src={imageURL} alt={`Preview ${index}`} style={{ maxWidth: '100px', maxHeight: '100px', margin: '5px' }} />
                    ))}
                    <button onClick={handleImageUpload}>Upload Images</button>
                </div>
                <div>
                    <button onClick={() => HandleQuestionSubmit(body, navigate)}>등록</button>
                </div>
            </div>
        </>
    );
}

export default MakingPost;