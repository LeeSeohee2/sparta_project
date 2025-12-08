package com.example.demo.controller.chat;

import com.example.demo.domain.user.Users;
import com.example.demo.dto.chat.ChatMessagePageResponse;
import com.example.demo.dto.chat.ChatMessageRequest;
import com.example.demo.dto.chat.ChatMessageResponse;
import com.example.demo.dto.chat.ReadStateUpdateRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    // ============================================
    // 현재 로그인 사용자 조회
    // ============================================
    private Users getCurrentUser() {
        Authentication auth = SecurityContextHolder
                .getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }


    // ============================================
    // 1) 메시지 조회 (cursor 기반)
    // GET /api/chatrooms/{roomId}/messages
    // ============================================
    @GetMapping("/chatrooms/{roomId}/messages")
    public ResponseEntity<ChatMessagePageResponse> getMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long cursor
    ) {
        Users me = getCurrentUser();

        ChatMessagePageResponse page = chatMessageService
                .getMessages(roomId, cursor, me.getUserId());

        return ResponseEntity.ok(page);
    }


    // ============================================
    // 2) 읽음 처리
    // PATCH /api/messages/{roomId}/read
    // ============================================
    @PatchMapping("/messages/{roomId}/read")
    public ResponseEntity<Void> updateReadState(
            @PathVariable Long roomId,
            @RequestBody ReadStateUpdateRequest request
    ) {
        Users me = getCurrentUser();
        chatMessageService.updateReadState(roomId, me.getUserId(), request.getLastReadMessageId());
        return ResponseEntity.ok().build();
    }
//    @PatchMapping("/messages/{roomId}/read")
//    public ResponseEntity<Void> updateReadState(
//            @PathVariable Long roomId,
//            @RequestBody ReadStateUpdateRequest request
//    ) {
//        Users me = getCurrentUser();
//
//        chatMessageService.updateReadState(
//                roomId,
//                me.getUserId(),
//                request.getLastReadMessageId()
//        );
//
//        return ResponseEntity.ok().build();
//    }


    // ============================================
    // 3) 메시지 전송
    // ⭐ API 명세에 맞게 URL 변경됨
    // POST /api/messages   ← 수정됨
    // ============================================
    @PostMapping("/messages")   // ⭐ 기존 "/chat/send" 에서 변경됨
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @RequestBody ChatMessageRequest request
    ) {
        Users me = getCurrentUser();

        // 강제 설정 (프론트가 senderId를 보내지 않아도 됨)
        request.setSenderId(me.getUserId());

        // 메시지 저장 + Redis 발행 + STOMP 전달
        return ResponseEntity.ok(
                chatMessageService.saveAndPublish(request)
        );
    }
}
