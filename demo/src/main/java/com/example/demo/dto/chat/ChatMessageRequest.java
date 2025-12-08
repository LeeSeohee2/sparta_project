package com.example.demo.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageRequest {

    private Long roomId;
    private Long senderId;   // ★ 반드시 있어야 함
    private String content;
    private String type;     // TEXT, IMAGE 등
}
