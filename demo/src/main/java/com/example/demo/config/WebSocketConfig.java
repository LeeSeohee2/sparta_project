package com.example.demo.config;


import org.springframework.context.annotation.Configuration;
// 이 클래스가 스프링 설정 파일임을 알려주는 어노테이션 사용을 위해 import

import org.springframework.messaging.simp.config.MessageBrokerRegistry;
// STOMP 메시지 브로커 설정(구독 경로 등)을 위한 클래스

import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
// WebSocket + STOMP 메시징 기능을 활성화하는 어노테이션 import

import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
// 클라이언트가 연결할 WebSocket/STOMP 엔드포인트 설정을 위한 클래스

import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
// WebSocket/STOMP 전체 설정을 커스터마이징할 때 사용하는 인터페이스

@Configuration
// Spring에게 “이 클래스는 설정(Config) 클래스다” 라고 알려주는 어노테이션

@EnableWebSocketMessageBroker
// STOMP 메시지 브로커 기능을 켠다 → WebSocket 위에 STOMP 프로토콜 사용 가능해짐

public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
// WebSocketMessageBrokerConfigurer 구현 → WebSocket/STOMP 설정을 재정의 가능하게 함

    @Override
    // 인터페이스의 메서드를 재정의한다는 의미

    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 최초로 WebSocket 연결을 시도할 때 접속하는 엔드포인트 설정

        registry.addEndpoint("/ws/chat")
                // WebSocket 연결 URL → 프론트에서 ws://서버주소/ws/chat 으로 연결함

                .setAllowedOriginPatterns("*")
                // 웹 브라우저의 CORS 정책을 풀어 모든 도메인에서 WebSocket 연결 허용

                .withSockJS();
        // WebSocket이 안 되는 환경(구버전 브라우저 포함)을 위해 SockJS fallback 기능 제공
    }

    @Override
    // 메시지 브로커 관련 설정을 재정의한다는 의미

    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // STOMP 메시지 브로커 경로를 설정하는 메서드

        registry.enableSimpleBroker("/topic");
        // subscribe(구독) 경로 설정 — 클라이언트는 /topic/** 를 구독하여 메시지를 받음
        // 예: /topic/chat/rooms/1 → roomId=1 채팅방 실시간 메시지 수신

        registry.setApplicationDestinationPrefixes("/pub");
        // send(메시지 전송) 경로 prefix 설정
        // 클라이언트는 /pub/** 로 메시지를 보내면 서버 Controller @MessageMapping 이 처리함
        // 예: /pub/chat/message → 메시지 전송 요청
    }
}