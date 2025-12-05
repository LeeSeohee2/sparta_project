/**
 * src/pages/project/SignupModal.jsx
 */
import React, { useState } from "react";
import "./Modal.css";
import { useNavigate } from "react-router-dom";
import axios from "axios"; // axios는 JWT가 필요 없는 요청에 사용

function SignupModal() {
    const [email, setEmail] = useState("");
    const [name, setName] = useState("");
    const [role, setRole] = useState("buyer"); // 기본값: 구매자
    const [password, setPassword] = useState("");

    const navigate = useNavigate();

    const handleSignup = async (e) => {
        e.preventDefault();

        const requestBody = {
            email,
            name,
            password,
            role
        };

        try {
            // JWT가 필요 없으므로 일반 axios 사용
            await axios.post("http://localhost:8080/api/auth/signup", requestBody);
            
            alert("회원가입 성공! 이제 로그인하세요.");

            // 로그인 화면으로 이동
            navigate("/");
        } catch (err) {
            console.error("회원가입 오류:", err.response?.data || err);
            alert("회원가입 실패! 입력 정보를 확인하거나 서버 오류가 발생했습니다.");
        }
    };

    return (
        <div className="modal-overlay">
            <form className="modal-content login-modal" onSubmit={handleSignup}>
                <div className="login-header">
                    <span className="chat-icon">👤</span> {/* 사용자 아이콘 */}
                    <h1>회원가입</h1>
                </div>

                <input
                    type="email"
                    placeholder="이메일"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />

                <input
                    type="text"
                    placeholder="이름"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                />

                <input
                    type="password"
                    placeholder="비밀번호"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />

                {/* 역할 선택 드롭다운 */}
                <select value={role} onChange={(e) => setRole(e.target.value)}>
                    <option value="buyer">구매자</option>
                    <option value="seller">판매자</option>
                </select>

                <button type="submit" className="btn-login-gradient">
                    회원가입
                </button>
            </form>
        </div>
    );
}

export default SignupModal;