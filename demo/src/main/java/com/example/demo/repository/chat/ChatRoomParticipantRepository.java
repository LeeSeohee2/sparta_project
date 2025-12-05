package com.example.demo.repository.chat;


import com.example.demo.domain.chat.ChatRoomParticipant;       // Participant 엔티티 import
import com.example.demo.domain.user.Users;                     // Users 엔티티 import
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;                                         // List 타입 사용

public interface ChatRoomParticipantRepository
        extends JpaRepository<ChatRoomParticipant, Long> {

    List<ChatRoomParticipant> findByUser(Users user);          // 특정 유저가 참여한 모든 상담방 조회

    List<ChatRoomParticipant> findByRoomId(Long roomId);       // 특정 상담방에 참여 중인 모든 참가자 조회
}