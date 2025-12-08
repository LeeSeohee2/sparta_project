package com.example.demo.controller.chat;


import com.example.demo.dto.chat.ChatRoomSummaryResponse;   // DTO import
import com.example.demo.dto.chat.InquiryCreateRequest;
import com.example.demo.service.chat.ChatRoomService;       // 서비스 import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                                             // REST API 컨트롤러
@RequestMapping("/api")                                     // 공통 URL prefix
@RequiredArgsConstructor                                    // 생성자 자동 주입
public class ChatRoomController {

    private final ChatRoomService chatRoomService;          // 채팅 서비스 의존성 주입

    // 상담 요청 생성 → 방 자동 생성
    @PostMapping("/inquiries")                              // POST /api/inquiries
    public ResponseEntity<ChatRoomSummaryResponse> createInquiry(
            @RequestBody InquiryCreateRequest request       // JSON body → DTO 로 받음
    ) {
        ChatRoomSummaryResponse response =
                chatRoomService.createInquiry(request);     // 서비스 호출하여 상담방 생성
        return ResponseEntity.ok(response);                 // 생성된 상담방 정보 반환
    }

    // 내가 참여한 상담방 목록 조회
    @GetMapping("/chatrooms/my")                            // GET /api/chatrooms/my
    public ResponseEntity<List<ChatRoomSummaryResponse>> getMyRooms() {

        List<ChatRoomSummaryResponse> rooms =
                chatRoomService.getMyRooms();               // 서비스에서 목록 조회

        return ResponseEntity.ok(rooms);                    // JSON 배열로 응답
    }

    // 판매자 또는 관리자가 상담방을 맡는 배정 기능
    @PostMapping("/chatrooms/{roomId}/assign")              // POST /api/chatrooms/{roomId}/assign
    public ResponseEntity<Void> assignRoom(@PathVariable Long roomId) {

        chatRoomService.assignRoom(roomId);                 // 서비스 호출로 배정 처리

        return ResponseEntity.ok().build();                 // 바디 없이 200 OK 만 반환
    }
    /** =========================
     *   3) 전체 unreadCount 반환 API
     * ========================= */
    @GetMapping("/chatrooms/unread/total")
    public ResponseEntity<Long> getTotalUnread() {
        Long total = chatRoomService.getTotalUnreadForCurrentUser();
        return ResponseEntity.ok(total);
    }



}