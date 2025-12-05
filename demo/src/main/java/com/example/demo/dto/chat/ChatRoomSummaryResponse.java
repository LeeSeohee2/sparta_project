package com.example.demo.dto.chat;


import lombok.Builder;
import lombok.Getter;

@Getter                                          // 필드에 대한 getter 생성
@Builder                                         // 빌더 패턴으로 객체 생성 가능
public class ChatRoomSummaryResponse {

    private Long roomId;                         // 상담방 ID
    private String productName;                  // 상품 이름
    private String lastMessage;                  // 최근 메시지 내용
    private String status;                       // 상담 상태(OPEN / ASSIGNED / CLOSED)
    private Long unreadCount;                    // 안 읽은 메시지 수(추후 ReadState 연동 시 사용)
}