import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axiosInstance from "../../api/axiosInstance";
import "./ChatList.css"; // 스타일 재사용

function ChatRooms() {
    const navigate = useNavigate();
    const [rooms, setRooms] = useState([]);

    // 상담방 목록 로드
    const fetchChatRooms = async () => {
        try {
            const res = await axiosInstance.get("/chatrooms/my");
            setRooms(res.data);
        } catch (e) {
            console.error("채팅방 목록 조회 실패:", e);
        }
    };

    useEffect(() => {
        fetchChatRooms();
    }, []);

    return (
        <div className="chat-list-container">
            <header className="chat-header-gradient">
                <span className="header-title">💬 상담 목록</span>
            </header>

            <main className="product-list-area">
                {rooms.map(room => (
                    <div 
                        key={room.roomId} 
                        className="list-item product-item"
                        onClick={() => navigate(`/chat/${room.roomId}`)}
                    >
                        <div className="avatar" style={{ backgroundColor: "#9fc5f8" }}>
                            💬
                        </div>

                        <div className="item-info">
                            <div className="item-name">
                                {room.productName || `상담방 ${room.roomId}`}
                            </div>

                            <div className="item-preview">
                                {room.lastMessage || "아직 메시지가 없습니다."}
                            </div>
                        </div>

                        <div className="item-meta">
                            {/* 🔥 unreadCount 배지 */}
                            {room.unreadCount > 0 && (
                                <div className="badge-unread">
                                    {room.unreadCount}
                                </div>
                            )}

                            <div className="item-time">
                                {room.lastMessageTime || ""}
                            </div>
                        </div>
                    </div>
                ))}
            </main>
        </div>
    );
}

export default ChatRooms;
