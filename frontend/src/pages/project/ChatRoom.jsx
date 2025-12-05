/**
 * src/pages/project/ChatRoom.jsx
 * - WebSocket(STOMP) 연결
 * - 메시지 전송
 * - 실시간 메시지 수신
 */

import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';

import SockJS from "sockjs-client/dist/sockjs";
import { Client } from "@stomp/stompjs";
import './ChatRoom.css';

function ChatRoom() {
   const { roomId } = useParams();
    const navigate = useNavigate();

    const [message, setMessage] = useState("");
    const [messages, setMessages] = useState([]);  
    const stompClientRef = useRef(null);

    //  1) 채팅방 메시지 최초 로드 --------------------------------------------------
    useEffect(() => {
        const fetchMessages = async () => {
            try {
                const res = await axiosInstance.get(
                    `/chatrooms/${roomId}/messages`
                );

                console.log("📨 초기 메시지:", res.data.messages);
                setMessages(res.data.messages);

                // 마지막 메시지가 있다면 → 읽음 처리
                if (res.data.messages.length > 0) {
                    const lastId = res.data.messages[res.data.messages.length - 1].messageId;

                    await axiosInstance.patch(`/messages/${roomId}/read`, {
                        lastReadMessageId: lastId
                    });
                }
            } catch (err) {
                console.error("초기 메시지 로드 실패", err);
            }
        };

        fetchMessages();
    }, [roomId]);

    //  2) WebSocket + STOMP 연결 ---------------------------------------------------
    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        if (!token) {
            alert("로그인이 필요합니다.");
            navigate("/");
            return;
        }

        const client = new Client({
            webSocketFactory: () => new SockJS("http://localhost:8080/ws/chat"),
            connectHeaders: {
                Authorization: `Bearer ${token}`
            },

            debug: (str) => console.log("STOMP:", str),

            onConnect: () => {
                console.log("📡 STOMP 연결 성공!");

                //  2-1) 방 구독
                client.subscribe(`/topic/chat/rooms/${roomId}`, (frame) => {
                    const msg = JSON.parse(frame.body);
                    console.log("📩 실시간 메시지:", msg);

                    setMessages(prev => [...prev, msg]);

                    //  2-2) 실시간 메시지도 즉시 읽음 처리
                    axiosInstance.patch(`/messages/${roomId}/read`, {
                        lastReadMessageId: msg.messageId
                    });
                });
            },

            onStompError: (frame) => {
                console.error("❌ STOMP ERROR:", frame);
            }
        });

        client.activate();
        stompClientRef.current = client;

        return () => client.deactivate();
    }, [roomId, navigate]);

    // 3) 메시지 전송 ---------------------------------------------------------------
    const handleSendMessage = (e) => {
        e.preventDefault();
        if (!message.trim()) return;

        const msgPayload = {
            roomId: Number(roomId),
            content: message,
            type: "TEXT"
        };

        stompClientRef.current.publish({
            destination: "/pub/chat/send",
            body: JSON.stringify(msgPayload)
        });

        setMessage("");
    };

    // UI ---------------------------------------------------------------------------
    return (
        <div className="chat-room-container">
            <header className="chat-header-gradient">
                <span className="back-button" onClick={() => navigate('/list')}>{'<'}</span>
                <div className="header-info">
                    <span className="chat-avatar-small">👤</span>
                    <span className="opponent-name">상담방 {roomId}</span>
                </div>
            </header>

            <main className="messages-area">
                {messages.map((msg, idx) => (
                    <div key={idx} className={`message-bubble ${msg.mine ? "me" : "opponent"}`}>
                        <div className="message-content">{msg.content}</div>
                        <div className="message-time">{msg.createdAt}</div>
                    </div>
                ))}
            </main>

            <form className="input-area" onSubmit={handleSendMessage}>
                <input
                    type="text"
                    placeholder="메시지를 입력하세요..."
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                />
                <button type="submit" className="send-button">🚀</button>
            </form>
        </div>
    );
}

export default ChatRoom;