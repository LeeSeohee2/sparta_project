package com.example.demo.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter                                                       // getter 생성
@AllArgsConstructor                                           // 전체 필드 생성자
@Builder                                                      // 빌더 사용
public class ChatMessagePageResponse {

    private List<ChatMessageResponse> messages;               // 현재 페이지 메시지 목록
    private Long nextCursor;                                  // 다음 페이지 조회용 커서(null 가능)

    // ⭐⭐⭐ [추가됨] 사용자가 아직 읽지 않은 메시지 개수 ⭐⭐⭐
    private Long unreadCount;                                 // unreadCount 필드 추가
}
