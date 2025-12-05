// src/api/axiosInstance.js

import axios from 'axios';

// 백엔드 API 기본 URL 설정
const API_BASE_URL = 'http://localhost:8080/api'; 

const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// 요청 인터셉터: JWT 토큰 자동 첨부
axiosInstance.interceptors.request.use(
    (config) => {
        // 'accessToken' 이름으로 저장된 JWT를 가져옵니다.
        const token = localStorage.getItem('accessToken'); 
        
        // 토큰이 있으면 Authorization 헤더에 Bearer 형태로 추가
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default axiosInstance;