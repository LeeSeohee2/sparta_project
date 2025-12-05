package com.example.demo.controller.chat;

import com.example.demo.domain.user.Users;                     // 유저 엔티티
import com.example.demo.dto.chat.ChatMessagePageResponse;      // 메시지 페이지 응답 DTO
import com.example.demo.dto.chat.ChatMessageRequest;
import com.example.demo.dto.chat.ChatMessageResponse;
import com.example.demo.dto.chat.ReadStateUpdateRequest;       // 읽음 상태 업데이트 요청 DTO
import com.example.demo.repository.UserRepository;             // 유저 레포지토리
import com.example.demo.service.chat.ChatMessageService;       // 채팅 메시지 서비스
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController                                               // REST API 컨트롤러
@RequestMapping("/api")                                       // 공통 prefix: /api
@RequiredArgsConstructor                                      // 생성자 주입 자동 생성
public class ChatMessageController {

    private final ChatMessageService chatMessageService;       // 메시지 서비스 의존성
    private final UserRepository userRepository;               // 유저 조회용 레포지토리

    // 현재 로그인한 사용자를 DB에서 찾아오는 유틸 메서드
    private Users getCurrentUser() {
        Authentication auth = SecurityContextHolder            // 시큐리티 컨텍스트에서
                .getContext().getAuthentication();             // Authentication 꺼냄
        String email = auth.getName();                         // JwtFilter에서 email을 principal로 넣어둠
        return userRepository.findByEmail(email)               // 이메일로 유저 조회
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // 1) 특정 채팅방의 메시지 목록 조회 (커서 기반)
    //    GET /api/chatrooms/{roomId}/messages?cursor=메시지ID
    @GetMapping("/chatrooms/{roomId}/messages")                // URL 매핑
    public ResponseEntity<ChatMessagePageResponse> getMessages(
            @PathVariable Long roomId,                         // 경로에서 방 ID 추출
            @RequestParam(required = false) Long cursor        // 쿼리스트링 cursor(선택)
    ) {
        Users me = getCurrentUser();                           // 현재 로그인한 사용자 조회

        ChatMessagePageResponse page = chatMessageService
                .getMessages(roomId, cursor, me.getUser_id()); // 서비스로 실제 메시지 조회

        return ResponseEntity.ok(page);                        // 200 OK + JSON 응답
    }

    // 2) 특정 방에 대해 "여기까지 읽었다" 표시하는 API
    //    PATCH /api/messages/{roomId}/read
    @PatchMapping("/messages/{roomId}/read")                   // URL 매핑
    public ResponseEntity<Void> updateReadState(
            @PathVariable Long roomId,                         // 경로에서 방 ID 추출
            @RequestBody ReadStateUpdateRequest request        // body에서 lastReadMessageId 추출
    ) {
        Users me = getCurrentUser();                           // 현재 로그인한 사용자 조회

        chatMessageService.updateReadState(                    // ReadState 갱신
                roomId,
                me.getUser_id(),
                request.getLastReadMessageId()
        );

        return ResponseEntity.ok().build();                    // 바디 없이 200 OK만 반환
    }
    // 3) 채팅 메시지 전송 API
    @PostMapping("/chat/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestBody ChatMessageRequest request
    ) {
        Users me = getCurrentUser();  // 현재 로그인 사용자

        // 강제 설정 (프론트에서 senderId를 보내지 않아도 됨)
        request.setSenderId(me.getUser_id());

        // 메시지 저장 + Redis 발행 + STOMP 전송
        return ResponseEntity.ok(
                chatMessageService.saveAndPublish(request)
        );
    }
}