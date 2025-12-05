package com.example.demo.repository.chat;


import com.example.demo.domain.chat.ChatRoom;          // ChatRoom 엔티티 import
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository
        extends JpaRepository<ChatRoom, Long> {        // <엔티티, PK 타입>
    // 기본 CRUD 메서드만으로도 생성/조회에 충분하므로 추가 메서드는 필요 없음
}