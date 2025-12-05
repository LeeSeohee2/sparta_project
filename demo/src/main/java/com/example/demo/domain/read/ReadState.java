package com.example.demo.domain.read;

import jakarta.persistence.*;                                  // JPA 어노테이션 import
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;                                // 업데이트 시간 기록용

@Entity                                                        // JPA 엔티티
@Table(name = "read_states")                                   // 실제 테이블 이름 지정
@Getter                                                       // 모든 필드 getter 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)             // JPA 기본 생성자
public class ReadState {

    @Id                                                       // PK 표시
    @GeneratedValue(strategy = GenerationType.IDENTITY)        // AUTO_INCREMENT
    private Long id;                                           // 읽음 상태 고유 ID

    @Column(nullable = false)                                  // null 불가
    private Long roomId;                                       // 어떤 방에 대한 읽음 상태인지

    @Column(nullable = false)                                  // null 불가
    private Long userId;                                       // 어떤 사용자의 읽음 상태인지

    @Column(nullable = false)                                  // null 불가
    private Long lastReadMessageId;                            // 마지막으로 읽은 메시지 ID

    @Column(nullable = false)                                  // null 불가
    private LocalDateTime updatedAt;                           // 마지막 업데이트 시각

    // 새 읽음 상태 생성용 생성자
    public ReadState(Long roomId, Long userId, Long lastReadMessageId) {
        this.roomId = roomId;                                  // 방 ID 저장
        this.userId = userId;                                  // 사용자 ID 저장
        this.lastReadMessageId = lastReadMessageId;            // 마지막 읽은 메시지 ID 저장
        this.updatedAt = LocalDateTime.now();                  // 생성 시각을 현재 시간으로 기록
    }

    // 이미 존재하는 읽음 상태를 갱신할 때 사용하는 메서드
    public void updateLastReadMessage(Long lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;            // 마지막 읽은 메시지 ID 변경
        this.updatedAt = LocalDateTime.now();                  // 변경 시각을 현재 시간으로 갱신
    }
}
