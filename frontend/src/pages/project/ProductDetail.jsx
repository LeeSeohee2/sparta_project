/**
 * src/pages/project/ProductDetail.jsx (신규: 상품 상세 및 문의 생성)
 * - /product/:id 경로에서 상품 상세를 로드하고, 문의를 생성합니다.
 */
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance'; 
import './Modal.css'; // 기본 스타일 재활용

function ProductDetail() {
    // URL에서 상품 ID를 가져옴
    const { id } = useParams(); 
    const navigate = useNavigate();

    // 상품 상세 정보 상태 (ProductDetailResponse DTO 예상)
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 1. 상품 상세 정보 로드
    useEffect(() => {
        const fetchProductDetail = async () => {
            try {
                // GET /api/products/{id} 호출
                const response = await axiosInstance.get(`/products/${id}`);
                setProduct(response.data);
                setLoading(false);
            } catch (err) {
                console.error("상품 상세 로드 오류:", err);
                setError("상품 정보를 불러오는 데 실패했습니다.");
                setLoading(false);
                if (err.response && err.response.status === 401) {
                    alert("인증 정보가 만료되었습니다. 다시 로그인해주세요.");
                    localStorage.removeItem('accessToken');
                    navigate('/');
                }
            }
        };

        if (id) {
            fetchProductDetail();
        }
    }, [id, navigate]);

    // 2. 문의하기 및 채팅방 생성
    const handleInquiry = async () => {
        if (!product) return;

        // 문의 내용 (임의의 텍스트 또는 입력 필드 추가 가능)
        const requestBody = {
            productId: product.id, // 백엔드 DTO에 맞게 product.productId 또는 product.id 사용
            content: `상품 ID ${product.id}에 대한 문의를 시작합니다.`
        };

        try {
            // POST /api/inquiries 호출 (채팅방 생성)
            const response = await axiosInstance.post('/inquiries', requestBody);
            
            // 백엔드가 생성된 채팅방 ID를 반환한다고 가정 (ChatRoomSummaryResponse의 roomId)
            const newRoomId = response.data.roomId; 

            alert(`채팅방이 생성되었습니다! 방 ID: ${newRoomId}`);
            
            // 3. 채팅방으로 이동
            navigate(`/chat/${newRoomId}`); 

        } catch (error) {
            console.error("채팅방 생성 오류:", error.response?.data || error);
            alert("문의(채팅방 생성)에 실패했습니다.");
        }
    };

    if (loading) { return <div className="loading-state">상품 상세 정보를 불러오는 중...</div>; }
    if (error) { return <div className="error-state">{error}</div>; }
    if (!product) { return <div className="error-state">상품을 찾을 수 없습니다.</div>; }


    // 렌더링 (UI)
    return (
        <div className="modal-overlay" style={{backgroundColor: '#f8f9fa', padding: '20px'}}> 
            <div className="modal-content" style={{ maxWidth: '600px', margin: 'auto', padding: '20px', backgroundColor: 'white', borderRadius: '10px', boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)' }}>
                <h1 style={{borderBottom: '2px solid #007bff', paddingBottom: '10px'}}>{product.name || `상품 ID: ${product.id}`}</h1>
                
                <p><strong>가격:</strong> {product.price || '정보 없음'} 원</p>
                <p><strong>재고:</strong> {product.stock || '?'} 개</p>
                
                <div style={{ marginTop: '20px', padding: '15px', border: '1px solid #ddd', borderRadius: '8px', backgroundColor: '#f0f8ff' }}>
                    <h3 style={{marginTop: '0'}}>상품 상세 설명</h3>
                    <p>{product.description || '상세 설명이 준비되지 않았습니다.'}</p>
                </div>

                <div style={{ marginTop: '30px', display: 'flex', gap: '10px' }}>
                    <button 
                        onClick={handleInquiry} 
                        className="btn-login-gradient"
                        style={{ flexGrow: 1 }}
                    >
                        📞 문의하기 (채팅방 생성)
                    </button>
                    <button 
                        onClick={() => navigate('/list')} 
                        style={{ padding: '10px 10px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: '8px', cursor: 'pointer' }}
                    >
                        목록으로 돌아가기
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ProductDetail;