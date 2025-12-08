package com.example.demo.controller.websocket;

import com.example.demo.dto.chat.ChatMessageRequest;        // 클라이언트가 보내는 메시지 DTO
import com.example.demo.dto.chat.ChatMessageResponse;       // 서버가 돌려보낼 메시지 DTO
import com.example.demo.service.chat.ChatMessageService;    // 메시지 저장 + 발행 비즈니스 로직
import lombok.RequiredArgsConstructor;                      // 생성자 자동 생성
import lombok.extern.slf4j.Slf4j;                           // 로그 출력용
import org.springframework.messaging.handler.annotation.MessageMapping; // STOMP 메시지를 처리하는 메서드에 붙이는 어노테이션
import org.springframework.messaging.handler.annotation.Payload;        // STOMP 메시지 바디를 바로 DTO 로 매핑할 때 사용
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;    // 헤더 접근(필요시)
import org.springframework.stereotype.Controller;                        // STOMP 컨트롤러는 @RestController 가 아닌 @Controller 사용

@Slf4j                                                     // log 사용 가능
@Controller                                               // View 를 쓰진 않지만 STOMP 용 컨트롤러이므로 @Controller 사용
@RequiredArgsConstructor                                   // final 필드를 매개변수로 받는 생성자 자동 생성
public class StompChatController {

    private final ChatMessageService chatMessageService;    // 메시지 저장 + Redis 발행을 담당하는 서비스

    @MessageMapping("/chat/send")                        // 클라이언트가 /pub/chat/message 로 send 하면 이 메서드가 호출됨
    public void handleChatMessage(@Payload ChatMessageRequest request,
                                  SimpMessageHeaderAccessor headerAccessor) {
        // @Payload: STOMP 메시지 바디(JSON)를 ChatMessageRequest 로 자동 매핑
        // headerAccessor: 필요할 경우 헤더에서 JWT, 세션 정보 등을 꺼낼 때 사용
        log.info("Received STOMP message: roomId={}, content={}",
                request.getRoomId(), request.getContent()); // 디버깅용 로그 출력

        // 현재는 SecurityContext 에서 사용자 정보를 꺼내서 사용
        // (JWT → WebSocket 핸드셰이크 연동 시 Authentication 에서 꺼낼 수 있음)
        ChatMessageResponse saved = chatMessageService.saveAndPublish(request); // 메시지 저장 + Redis 발행

        // 이 컨트롤러에서는 직접 convertAndSend 를 호출하지 않음
        // RedisSubscriber 가 Redis Pub/Sub 을 통해 수신 후 /topic/chat/rooms/{roomId} 로 브로드캐스트함
    }
}