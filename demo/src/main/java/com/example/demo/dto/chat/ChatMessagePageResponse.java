package com.example.demo.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter                                                       // getter 생성
@AllArgsConstructor                                           // 전체 필드 생성자
@Builder                                                      // 빌더 사용
public class ChatMessagePageResponse {

    private List<ChatMessageResponse> messages;               // 현재 페이지에 포함된 메시지 리스트
    private Long nextCursor;                                  // 다음 페이지 조회용 커서(더 없으면 null)
}