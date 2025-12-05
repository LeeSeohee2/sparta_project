/**
 * src/App.jsx (최종 라우팅 설정)
 */
import React from 'react';
import { Route, Routes, Navigate } from "react-router-dom" 
import LoginModal from "./pages/project/LoginModal"
import SignupModal from "./pages/project/SignupModal"
// import ProductPopup from "./pages/project/ProductPopup"; // 팝업은 이제 사용하지 않습니다.
 // ★ 새로 추가될 상세 페이지
import ChatList from "./pages/project/ChatList"; 
import ChatRoom from './pages/project/ChatRoom'; 
import ProductDetail from './pages/project/ProductDetail';
import ChatRooms from './pages/project/ChatRooms';


function App() {
    return (
        <Routes>
            {/* 1. 인증/회원가입 */}
            <Route path="/" element={<LoginModal />} />
            <Route path="/signup" element={<SignupModal />} />
            
            {/* 2. 상품 목록 페이지 (이전의 /list 경로 사용) */}
            <Route 
                path="/list" 
                element={<ChatList />} 
            />
            
            {/* 3. 상품 상세 페이지 (ID 파라미터 사용) */}
            <Route 
                path="/product/:id" 
                element={<ProductDetail />} // ★ 신규 컴포넌트 사용
            />

            {/* 4. 채팅방 상세 페이지 */}
            <Route
                path="/chat/:roomId" 
                element={<ChatRoom />}
            />
            <Route path="/rooms" element={<ChatRooms />} />
            {/* 5. 기타 경로 처리 */}
            <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
    )
}

export default App