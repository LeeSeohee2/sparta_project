package com.example.demo.domain.chat;


import com.example.demo.domain.user.Users;                // Users 엔티티 import
import jakarta.persistence.*;                             // JPA 관련 import
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;                           // 참여 시각 기록용

@Entity                                                   // JPA 엔티티 지정
@Table(name = "chat_room_participants")                   // 테이블명 지정
@Getter                                                   // getter 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)        // 기본 생성자(protected)
public class ChatRoomParticipant {

    @Id                                                   // PK 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // AUTO_INCREMENT
    private Long id;                                      // participant_id 에 해당

    @ManyToOne(fetch = FetchType.LAZY)                    // 다대일: 여러 참여자가 한 방에 속할 수 있음
    @JoinColumn(name = "room_id")                         // room_id FK 컬럼
    private ChatRoom room;                                // 어떤 상담방인지

    @ManyToOne(fetch = FetchType.LAZY)                    // 다대일: 여러 참여자가 한 사용자일 수 있음
    @JoinColumn(name = "userId")                          // user_id FK 컬럼
    private Users user;                                   // 어떤 사용자인지

    @Column(nullable = false)                             // null 불가
    private String role;                                  // buyer / seller / admin 역할

    @Column(nullable = false)                             // null 불가
    private LocalDateTime joinedAt;                       // 이 방에 참여한 시각

    private Long lastReadMessageId;                       // 마지막으로 읽은 메시지 ID(읽음 처리용)

    @Builder                                              // 빌더 사용
    public ChatRoomParticipant(ChatRoom room,             // 방
                               Users user,                // 사용자
                               String role) {             // 역할
        this.room = room;                                 // 필드에 값 설정
        this.user = user;
        this.role = role;
        this.joinedAt = LocalDateTime.now();              // 참여 시각을 현재 시각으로 설정
        this.lastReadMessageId = null;                    // 처음엔 아무 메시지도 안 읽었으므로 null
    }

    public void updateLastReadMessage(Long messageId) {   // 읽은 위치를 업데이트하는 메서드
        this.lastReadMessageId = messageId;               // 파라미터로 받은 messageId 저장
    }
}