// src/pages/project/ChatList.jsx
// - 상품 목록 화면
// - 상단에 "상담방 목록 보기" 버튼 추가
// - footer 정상 포함 (CSS 충돌 방지)

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance';
import './ChatList.css';

function ChatList() {
    const navigate = useNavigate();
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // 상품 목록 불러오기
    const fetchProducts = async () => {
        try {
            const response = await axiosInstance.get('/products');
            setProducts(response.data);
            setLoading(false);
        } catch (err) {
            console.error('상품 목록 로드 오류:', err);
            setError('상품 목록을 불러오는 데 실패했습니다.');
            setLoading(false);

            if (err.response && err.response.status === 401) {
                alert('인증 정보가 만료되었습니다. 다시 로그인해주세요.');
                localStorage.removeItem('accessToken');
                navigate('/');
            }
        }
    };

    // 최초 실행
    useEffect(() => {
        if (!localStorage.getItem('accessToken')) {
            navigate('/');
            return;
        }
        fetchProducts();
    }, []);

    // 상품 클릭 → 상세 페이지 이동
    const handleProductClick = (productId) => {
        navigate(`/product/${productId}`);
    };

    if (loading) {
        return <div className="loading-state">상품 목록을 불러오는 중...</div>;
    }
    if (error) {
        return <div className="error-state">{error}</div>;
    }

    return (
        <div className="chat-list-container">

            {/* 헤더 */}
            <header className="chat-header-gradient">
                <span className="header-title">상품 목록</span>
                <div className="header-icons">
                    <span className="search-icon">🔍</span>
                    <span className="settings-icon">⚙️</span>
                </div>
            </header>

            {/* 🔽 상담방 목록으로 가는 버튼 */}
            <button
                className="chat-room-btn"
                onClick={() => navigate('/rooms')}
            >
                💬 상담방 목록 보기
            </button>

            {/* 본문 */}
            <main className="product-list-area">
                <h3 style={{ padding: '10px 15px', color: '#555' }}>
                    🛍️ 상품 목록 (클릭 시 상세 페이지 이동)
                </h3>

                {products.length === 0 ? (
                    <div className="no-items">표시할 상품이 없습니다.</div>
                ) : (
                    products.map((product) => (
                        <div
                            key={product.id}
                            className="list-item product-item"
                            onClick={() => handleProductClick(product.id)}
                        >
                            <div
                                className="avatar"
                                style={{ backgroundColor: '#FFD700' }}
                            >
                                P
                            </div>

                            <div className="item-info">
                                <div className="item-name">
                                    {product.name || `상품 ${product.id}`}
                                </div>
                                <div className="item-preview">
                                    가격: {product.price} 원
                                </div>
                            </div>

                            <div className="item-meta">
                                <div className="item-time">
                                    재고: {product.stock || '?'}
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </main>

            {/* 🔽 footer 추가 (CSS 대응) */}
            <footer className="nav-bar">
                <span className="nav-icon active">🛍️ 상품</span>
                <span className="nav-icon" onClick={() => navigate('/rooms')}>
                    💬 상담방
                </span>
                <span className="nav-icon">👤 프로필</span>
            </footer>
        </div>
    );
}

export default ChatList;
