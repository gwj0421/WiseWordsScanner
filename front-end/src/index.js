import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import axios from "axios";
import {useNavigate} from "react-router-dom";

const root = ReactDOM.createRoot(document.getElementById('root'));

export const httpClientForCredentials = axios.create({
    baseURL: `http://localhost:8000`,
    // 서버와 클라이언트가 다른 도메인일 경우 필수
    withCredentials: true
});
httpClientForCredentials.interceptors.response.use(
    (response) => {
        return response;
    },
    error => {
        if (error.response.status === 401) {
            window.location.replace('/login');
        } else {
            console.log("error : ", error);
        }
        return Promise.reject(error);
    }
);
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
