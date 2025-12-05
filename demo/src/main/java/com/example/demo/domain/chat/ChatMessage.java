package com.example.demo.domain.chat;                          // 채팅 도메인 패키지

import com.example.demo.domain.user.Users;                     // 보낸 사람(유저) 엔티티 import
import jakarta.persistence.*;                                  // JPA 관련 어노테이션 import
import lombok.*;

import java.time.LocalDateTime;                                // 생성 시간 타입

@Entity                                                        // JPA 엔티티임을 표시
@Table(name = "chat_messages")                                 // 매핑될 실제 테이블 이름 지정
@Getter
@Setter  // 모든 필드에 대해 getter 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)             // JPA용 기본 생성자 (외부에서 사용 금지)
public class ChatMessage {

    @Id                                                       // PK 필드임을 표시
    @GeneratedValue(strategy = GenerationType.IDENTITY)        // AUTO_INCREMENT 방식으로 ID 생성
    private Long id;                                           // 메시지 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)                         // N:1 관계 (여러 메시지 → 하나의 방)
    @JoinColumn(name = "room_id", nullable = false)            // FK 컬럼 이름: room_id
    private ChatRoom room;                                     // 어떤 채팅방에서 보낸 메시지인지

    @ManyToOne(fetch = FetchType.LAZY)                         // N:1 관계 (여러 메시지 → 한 사용자)
    @JoinColumn(name = "sender_id", nullable = false)          // FK 컬럼 이름: sender_id
    private Users sender;                                      // 메시지 보낸 사람

    @Column(nullable = false, length = 20)                     // null 불가, 길이 20 제한
    private String type;                                       // 메시지 타입(TEXT / IMAGE / SYSTEM 등)

    @Column(nullable = false, columnDefinition = "TEXT")       // 긴 문자열 저장용 TEXT 컬럼
    private String content;                                    // 실제 메시지 내용

    @Column(nullable = false)                                  // null 불가
    private LocalDateTime createdAt;                           // 메시지 생성 시각

    @Builder                                                  // 빌더 패턴으로 객체 생성 가능하도록
    public ChatMessage(ChatRoom room,
                       Users sender,
                       String type,
                       String content) {
        this.room = room;                                      // 어느 방에서 온 메시지인지 설정
        this.sender = sender;                                  // 보낸 사람 설정
        this.type = type;                                      // 메시지 타입 설정
        this.content = content;                                // 메시지 내용 설정
        this.createdAt = LocalDateTime.now();                  // 생성 시각을 현재 시간으로 기록
    }
}
