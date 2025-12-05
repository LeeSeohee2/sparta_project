package com.example.demo.dto.chat;

import com.example.demo.domain.chat.ChatMessage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageResponse {

    private Long messageId;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String content;
    private String type;
    private String createdAt;
    private boolean mine;

    public static ChatMessageResponse from(ChatMessage m) {
        return ChatMessageResponse.builder()
                .messageId(m.getId())
                .roomId(m.getRoom().getId())
                .senderId(m.getSender().getUser_id())
                .senderName(m.getSender().getName())
                .content(m.getContent())
                .type(m.getType())
                .createdAt(m.getCreatedAt().toString())
                .mine(false)
                .build();
    }
}
