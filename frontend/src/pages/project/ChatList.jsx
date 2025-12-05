/**
 * src/pages/project/ChatList.jsx (상품 목록 로드 및 상세 페이지 이동)
 */
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../../api/axiosInstance'; 
import './ChatList.css'; 
// (ProductListResponse DTO 필드명은 productId, name, price 등으로 가정합니다.)

function ChatList() {
    const navigate = useNavigate();
    const [products, setProducts] = useState([]); 
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchProducts = async () => {
        
        try {
            const response = await axiosInstance.get('/products');
            setProducts(response.data); 
            setLoading(false);
        } catch (err) {
            console.error("상품 목록 로드 오류:", err);
            setError("상품 목록을 불러오는 데 실패했습니다.");
            setLoading(false);
            if (err.response && err.response.status === 401) {
                alert("인증 정보가 만료되었습니다. 다시 로그인해주세요.");
                localStorage.removeItem('accessToken');
                navigate('/');
            }
        }
    };

    useEffect(() => {
        if (!localStorage.getItem('accessToken')) {
            navigate('/');
            return;
        }
        fetchProducts();
    }, [navigate]);

    // ★ 리스트 항목 클릭 핸들러 (상품 상세 페이지로 이동)
    const handleProductClick = (productId) => {
        // /product/:id 경로로 이동
        navigate(`/product/${productId}`); 
    };

    if (loading) { return <div className="loading-state">상품 목록을 불러오는 중...</div>; }
    if (error) { return <div className="error-state">{error}</div>; }
    
    // 렌더링 (UI)
    return (
        <div className="chat-list-container">
            <header className="chat-header-gradient">
                <span className="header-title">상품 목록</span>
                <div className="header-icons">
                    <span className="search-icon">🔍</span>
                    <span className="settings-icon">⚙️</span>
                </div>
            </header>

            <main className="product-list-area">
                <h3 style={{padding: '10px 15px', color: '#555'}}>🛍️ 상품 목록 (클릭 시 상세 페이지 이동)</h3>
                
                {products.length === 0 ? (
                    <div className="no-items">표시할 상품이 없습니다.</div>
                ) : (
                    products.map((product) => (
                        <div 
                            key={product.id} 
                            className="list-item product-item"
                            onClick={() => handleProductClick(product.id)}
                        >
                            <div className="avatar" style={{backgroundColor: '#FFD700'}}>P</div>
                            <div className="item-info">
                                <div className="item-name">{product.name || `상품 ${product.id}`}</div>
                                <div className="item-preview">가격: {product.price || '정보 없음'} 원</div>
                            </div>
                            <div className="item-meta">
                                <div className="item-time">재고: {product.stock || '?'}</div> 
                            </div>
                        </div>
                    ))
                )}
            </main>

            <footer className="nav-bar">
                <span className="nav-icon active">🛍️ 상품</span>
                <span className="nav-icon" onClick={() => navigate('/list')}>💬 채팅 목록</span> {/* 임시 */}
                <span className="nav-icon">👤 프로필</span>
            </footer>
        </div>
    );
}

export default ChatList;