/**
 * src/pages/project/ProductPopup.jsx (렌더링 테스트 및 간소화 버전)
 * * - axiosInstance 호출 로직 일시적으로 제거
 * - UI에 필수 요소만 남겨 오류 가능성 최소화
 */
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// import axiosInstance from '../../api/axiosInstance'; // API 관련 코드는 잠시 주석 처리합니다.
import './Modal.css'; // ★ CSS 파일 경로는 같은 폴더에 있다고 가정합니다.

function ProductPopup() {
    // 상품 ID와 내용은 일단 useState로 유지합니다.
    const [productId, setProductId] = useState(1); 
    const [content, setContent] = useState("이 상품에 대해 문의드립니다.");
    const navigate = useNavigate();

    // 1. 상담 요청 로직 (임시 함수: 실제 API 호출은 하지 않음)
    const handleCreateInquiry = (e) => {
        e.preventDefault();
        alert(`상품 ID: ${productId}로 문의를 요청했습니다. (API 호출 생략)`);
        
        // 채팅 목록 페이지로 이동
        navigate("/list");
    };S
    
    // 2. 임시 건너뛰기 버튼 (바로 채팅 목록으로 이동)
    const handleSkip = () => {
        navigate("/list");
    };

    return (
        // UI가 명확히 보이도록 배경색을 추가했습니다.
        <div className="modal-overlay" style={{backgroundColor: 'rgba(0, 0, 0, 0.7)'}}> 
            <form 
                className="modal-content login-modal" 
                style={{ padding: '30px', maxWidth: '500px' }} 
                onSubmit={handleCreateInquiry}
            >
                <h2>상품 문의 및 채팅방 생성</h2>
                <p>백엔드 테스트를 위해 문의를 생성하거나 목록으로 바로 이동합니다.</p>
                
                {/* 상품 ID 입력 필드 */}
                <input
                    type="number"
                    placeholder="상품 ID"
                    value={productId}
                    onChange={(e) => setProductId(e.target.value)}
                    style={{width: '100%', padding: '12px', marginBottom: '15px', border: '1px solid #ddd', borderRadius: '8px', boxSizing: 'border-box'}}
                    required
                />
                
                {/* 문의 내용 입력 필드 */}
                <textarea
                    placeholder="문의 내용을 입력하세요."
                    rows="4"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    style={{width: '100%', padding: '12px', marginBottom: '15px', border: '1px solid #ddd', borderRadius: '8px', boxSizing: 'border-box'}}
                    required
                />

                {/* 상담방 생성 버튼 (폼 제출) */}
                <button type="submit" className="btn-login-gradient" style={{marginTop: '0'}}>
                    상담방 생성 및 문의하기 (바로 목록으로 이동)
                </button>
                
                {/* 건너뛰기 버튼 */}
                <button 
                    type="button" 
                    onClick={handleSkip} 
                    style={{
                        width: '100%', 
                        padding: '10px 0', 
                        marginTop: '10px',
                        backgroundColor: '#6c757d', 
                        color: 'white',
                        border: 'none',
                        borderRadius: '8px',
                        cursor: 'pointer'
                    }}
                >
                    채팅 목록으로 바로 건너뛰기
                </button>
            </form>
        </div>
    );
}

export default ProductPopup;