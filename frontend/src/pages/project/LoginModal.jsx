/**
 * src/pages/project/LoginModal.jsx (로그인 통신 및 토큰 저장 복구)
 */
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios'; 
import './Modal.css'; 

function LoginModal() {
    const [emailOrId, setEmailOrId] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    // 로그인 처리 함수: 이메일/ID와 비밀번호로 JWT를 받아 저장 후 페이지 이동
    const handleLogin = async (e) => {
        e.preventDefault(); 

        const requestBody = {
            email: emailOrId, 
            password: password
        };

        try {
            const response = await axios.post("http://localhost:8080/api/auth/login", requestBody);
            
            console.log("ㅇㅇ",response);
            const { accessToken } = response.data;
            localStorage.setItem("accessToken", accessToken);
            localStorage.setItem("userId", response.data.userId);
            alert("로그인 성공! 토큰이 저장되었으며 상품 목록 페이지로 이동합니다.");

            // 4. ★ 수정: 상품 목록 페이지 (/list)로 이동
            navigate("/list"); 

        } catch (error) {
            console.error("로그인 요청 오류:", error.response?.data || error);
            alert("로그인 실패! 이메일 또는 비밀번호를 확인해주세요."); 
        }
    };
    
    return (

        <div className="modal-overlay"> 
            <form className="modal-content login-modal" onSubmit={handleLogin}>
                <div className="login-header">
                    <span className="chat-icon">💬</span>
                    <h1>환영합니다!</h1>
                </div>
                
                <input 
                    type="text" 
                    placeholder="이메일 또는 아이디"
                    value={emailOrId}
                    onChange={(e) => setEmailOrId(e.target.value)}
                    required
                />

                <input 
                    type="password" 
                    placeholder="비밀번호"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                
                <button 
                    type="submit"
                    className="btn-login-gradient"
                >
                    로그인
                </button>
                
                <div className="login-links">
                    <span>계정이 없으신가요?</span>
                    <a href="/signup" className="link">회원가입</a>| 
                    <a href="#" className="link">비밀번호 찾기</a>
                </div>
            </form>
        </div>
    );
}

export default LoginModal;