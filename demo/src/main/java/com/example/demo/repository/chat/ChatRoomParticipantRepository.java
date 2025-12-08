
package com.example.demo.repository.chat;

import com.example.demo.domain.chat.ChatRoomParticipant;
import com.example.demo.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomParticipantRepository
        extends JpaRepository<ChatRoomParticipant, Long> {

    // Users 엔티티 전체를 기준으로 검색
    List<ChatRoomParticipant> findByUser(Users user);

    // Users.userId 기준으로 조회
    List<ChatRoomParticipant> findByUser_UserId(Long userId);
    // 방 기준 검색
    List<ChatRoomParticipant> findByRoomId(Long roomId);
}



//package com.example.demo.repository.chat;
//
//
//import com.example.demo.domain.chat.ChatRoomParticipant;       // Participant 엔티티 import
//import com.example.demo.domain.user.Users;                     // Users 엔티티 import
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;                                         // List 타입 사용
//
//public interface ChatRoomParticipantRepository
//        extends JpaRepository<ChatRoomParticipant, Long> {
//    // 기존: 엔티티 기반 조회 (그대로 유지)
//    List<ChatRoomParticipant> findByUser(Users user);
//
//    // 추천: userId만으로 조회 (서비스 단에서 엔티티 만들 필요 없음)
//    List<ChatRoomParticipant> findByUserId(Long userId);
//
//    // 방 ID 기반 조회
//    List<ChatRoomParticipant> findByRoomId(Long roomId);
//}