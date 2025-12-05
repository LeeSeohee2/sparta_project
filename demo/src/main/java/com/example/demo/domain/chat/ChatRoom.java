package com.example.demo.domain.chat;


import com.example.demo.domain.product.Product;          // 상품 엔티티 import
import jakarta.persistence.*;                            // JPA 관련 import
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;                          // 생성/종료 시간 저장용

@Entity                                                  // JPA 엔티티 지정
@Table(name = "chat_rooms")                              // 테이블명 chat_rooms 로 지정
@Getter                                                  // getter 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)       // 기본 생성자(protected) 생성
public class ChatRoom {

    @Id                                                  // PK 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // AUTO_INCREMENT
    private Long id;                                     // room_id 에 해당

    @ManyToOne(fetch = FetchType.LAZY)                   // 다대일 관계: 여러 방이 하나의 상품에 연결
    @JoinColumn(name = "product_id")                     // product_id FK 컬럼
    private Product product;                             // 이 상담이 진행되는 상품

    @Column(nullable = false)                            // null 불가
    private String status;                               // 상담 상태: OPEN / ASSIGNED / CLOSED

    @Column(nullable = false)                            // null 불가
    private LocalDateTime createdAt;                     // 상담방 생성 시각

    private LocalDateTime closedAt;                      // 상담방 종료 시각(종료 전에는 null)

    @Builder                                             // 빌더 패턴 사용 가능
    public ChatRoom(Product product, String status) {    // 상담방 생성 시 필요한 값들만 받는 생성자
        this.product = product;                          // 연결된 상품 설정
        this.status = status;                            // 초기 상태 설정(보통 "OPEN")
        this.createdAt = LocalDateTime.now();            // 생성 시각을 현재 시각으로 설정
    }

    public void assign() {                               // 상담 담당자가 배정될 때 호출
        this.status = "ASSIGNED";                        // 상태를 ASSIGNED 로 변경
    }

    public void close() {                                // 상담 종료 시 호출
        this.status = "CLOSED";                          // 상태를 CLOSED 로 변경
        this.closedAt = LocalDateTime.now();             // 종료 시각 기록
    }
}
