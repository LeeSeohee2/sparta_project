package com.example.demo.repository.chat;                      // 채팅 레포지토리 패키지

import com.example.demo.domain.chat.ChatMessage;               // 메시지 엔티티 import
import com.example.demo.domain.chat.ChatRoom;                  // 채팅방 엔티티 import
import org.springframework.data.jpa.repository.JpaRepository;  // JPA 레포지토리 기본 인터페이스
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository                                                    // 레포지토리 빈으로 등록
public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Long> {             // <엔티티, PK 타입>

    // 특정 채팅방의 가장 마지막 메시지 조회
    Optional<ChatMessage>  findTopByRoomOrderByCreatedAtDesc(ChatRoom room);

    // 특정 방의 최신 50개 메시지를 ID 내림차순으로 조회
    List<ChatMessage> findTop50ByRoomOrderByIdDesc(ChatRoom room);

    // 특정 방에서, 주어진 ID보다 작은 메시지 중 최신 50개 조회(커서 기반 페이지네이션용)
    List<ChatMessage> findTop50ByRoomAndIdLessThanOrderByIdDesc(ChatRoom room, Long id);

    // 방 기준으로 가장 최근 메시지 1개 조회 (방 목록 lastMessage 용)
    Optional<ChatMessage> findTopByRoomOrderByIdDesc(ChatRoom room);

    boolean existsByRoomAndIdLessThan(ChatRoom room, Long id);

    // 특정 방에서, 기준 메시지 ID보다 큰(= 아직 안 읽은) 메시지 개수 조회
    long countByRoomAndIdGreaterThan(ChatRoom room, Long lastReadMessageId);


    @Query("select max(m.id) from ChatMessage m where m.room = :room")
    Long findLatestMessageId(ChatRoom room);

    @Query("""
        select count(m)
        from ChatMessage m
        where m.room = :room
          and m.id > :lastReadMessageId
          and m.sender.id <> :userId
    """)
    Long countUnreadMessages(ChatRoom room, Long lastReadMessageId, Long userId);




}
