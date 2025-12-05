package com.example.demo.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisMessageListenerConfig {

    private final RedisConnectionFactory connectionFactory;  // Redis 연결 팩토리
    private final RedisSubscriber redisSubscriber;           // Redis 구독 처리 서비스

    /**
     * Redis Pub/Sub 에서 사용할 채널(topic)을 빈으로 등록
     */
    @Bean
    public ChannelTopic chatTopic() {
        return new ChannelTopic("chat-room");  // publisher 에서 보낸 채널명과 동일해야 한다
    }

    /**
     * Redis 메시지를 스프링 애플리케이션으로 받아 처리하는 컨테이너
     */
    @Bean
    public RedisMessageListenerContainer redisContainer(
            MessageListenerAdapter listenerAdapter,
            ChannelTopic chatTopic
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 특정 topic 에 대해 메시지가 오면 listenerAdapter 실행
        container.addMessageListener(listenerAdapter, chatTopic);

        return container;
    }

    /**
     * Redis 에서 메시지를 받으면 RedisSubscriber.handleMessage(...) 를 호출하도록 연결
     */
    @Bean
    public MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(redisSubscriber, "handleMessage");
    }
}
