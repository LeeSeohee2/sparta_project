package com.example.demo.websocket;

import com.example.demo.dto.chat.ChatMessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public void handleMessage(String publishedMessage) {
        System.out.println("📥 [RedisSubscriber] RAW = " + publishedMessage);
        try {
            ChatMessageResponse response =
                    objectMapper.readValue(publishedMessage, ChatMessageResponse.class);

            messagingTemplate.convertAndSend(
                    "/topic/chat/rooms/" + response.getRoomId(),
                    response
            );

        } catch (Exception e) {
            log.error("❌ Redis 메시지 처리 실패", e);
        }
    }
}
