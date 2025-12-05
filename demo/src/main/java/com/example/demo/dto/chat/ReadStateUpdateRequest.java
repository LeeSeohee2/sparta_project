package com.example.demo.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter                                                       // getter
@Setter                                                       // setter
@NoArgsConstructor                                            // 기본 생성자
public class ReadStateUpdateRequest {

    private Long lastReadMessageId;                           // 마지막으로 읽은 메시지 ID
}