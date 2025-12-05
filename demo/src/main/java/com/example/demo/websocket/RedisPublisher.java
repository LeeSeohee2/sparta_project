package com.example.demo.websocket;

import com.example.demo.dto.chat.ChatMessageResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic chatTopic;

    public void publish(ChatMessageResponse message) {

        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(chatTopic.getTopic(), json);
            log.info("Published message → Redis: {}", json);
        } catch (JsonProcessingException e) {
            log.error("Redis publish 실패", e);
        }
    }
}
