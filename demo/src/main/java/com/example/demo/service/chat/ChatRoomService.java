package com.example.demo.service.chat;


import com.example.demo.domain.chat.ChatMessage;                // 엔티티들 import
import com.example.demo.domain.chat.ChatRoom;
import com.example.demo.domain.chat.ChatRoomParticipant;
import com.example.demo.domain.product.Product;
import com.example.demo.domain.user.Users;
import com.example.demo.dto.chat.ChatRoomSummaryResponse;       // DTO import
import com.example.demo.dto.chat.InquiryCreateRequest;
import com.example.demo.repository.UserRepository;              // 레포지토리 import
import com.example.demo.repository.chat.ChatMessageRepository;
import com.example.demo.repository.chat.ChatRoomParticipantRepository;
import com.example.demo.repository.chat.ChatRoomRepository;
import com.example.demo.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;                               // stream 사용을 위한 import

@Service                                                        // 서비스 빈 등록
@RequiredArgsConstructor                                        // final 필드 생성자 자동 주입
public class ChatRoomService {

    private final ProductRepository productRepository;          // 상품 조회용 레포지토리
    private final ChatRoomRepository chatRoomRepository;        // 상담방 레포지토리
    private final ChatRoomParticipantRepository participantRepository; // 참여자 레포지토리
    private final ChatMessageRepository chatMessageRepository;  // 메시지 레포지토리
    private final UserRepository userRepository;                // 사용자 레포지토리

    // 현재 로그인한 사용자의 Users 엔티티를 조회하는 공통 메서드
    private Users getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();                          // JwtFilter 에서 principal 로 email 설정함
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // 상담 요청 생성 → 상담방 자동 생성
    @Transactional                                              // 하나의 트랜잭션으로 묶기
    public ChatRoomSummaryResponse createInquiry(InquiryCreateRequest request) {

        Users buyer = getCurrentUser();                         // 로그인한 사용자 = 구매자

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        // productId 로 상품 조회, 없으면 예외

        Users seller = product.getSeller();                     // 상품에 연결된 판매자 조회
        if (seller == null) {                                   // 판매자가 없으면 에러 처리
            throw new IllegalStateException("해당 상품에 판매자가 지정되지 않았습니다.");
        }

        ChatRoom room = ChatRoom.builder()                      // 상담방 엔티티 생성
                .product(product)                               // 어떤 상품에 대한 상담인지 설정
                .status("OPEN")                                // 초기 상태 OPEN
                .build();
        chatRoomRepository.save(room);                          // DB 에 저장

        ChatRoomParticipant buyerParticipant = ChatRoomParticipant.builder()
                .room(room)                                     // 생성된 상담방
                .user(buyer)                                    // 현재 로그인한 구매자
                .role("buyer")                                  // 역할 buyer
                .build();
        participantRepository.save(buyerParticipant);           // 참여자 저장

        ChatRoomParticipant sellerParticipant = ChatRoomParticipant.builder()
                .room(room)                                     // 동일한 방
                .user(seller)                                   // 판매자
                .role("seller")                                 // 역할 seller
                .build();
        participantRepository.save(sellerParticipant);          // 참여자 저장

        ChatMessage firstMessage = ChatMessage.builder()        // 첫 메시지 엔티티 생성
                .room(room)                                     // 방
                .sender(buyer)                                  // 구매자가 보낸 문의
                .type("TEXT")                                   // 텍스트 타입
                .content(request.getContent())                  // 문의 내용 그대로 저장
                .build();
        chatMessageRepository.save(firstMessage);               // 메시지 저장

        return ChatRoomSummaryResponse.builder()                 // 응답 DTO 생성
                .roomId(room.getId())
                .productName(product.getName())
                .lastMessage(firstMessage.getContent())
                .status(room.getStatus())
                .unreadCount(0L)                                // unread 계산은 추후 ReadState 에서 처리
                .build();
    }

    // 내가 참여한 모든 상담방 목록 조회
    @Transactional(readOnly = true)                             // 조회 전용 트랜잭션
    public List<ChatRoomSummaryResponse> getMyRooms() {

        Users me = getCurrentUser();                            // 현재 로그인한 사용자

        List<ChatRoomParticipant> myParticipants =
                participantRepository.findByUser(me);           // 내가 참여 중인 모든 방 조회

        return myParticipants.stream()                          // stream 으로 변환
                .map(participant -> {                           // 각 participant 를 응답 DTO 로 매핑
                    ChatRoom room = participant.getRoom();      // 참여 중인 방

                    ChatMessage lastMessage = chatMessageRepository
                            .findTopByRoomOrderByCreatedAtDesc(room)
                            .orElse(null);                      // 최근 메시지 한 개 조회

                    String lastContent =                         // 최근 메시지 내용이 없을 수도 있으므로 처리
                            (lastMessage != null) ? lastMessage.getContent() : "";

                    return ChatRoomSummaryResponse.builder()    // DTO 생성
                            .roomId(room.getId())
                            .productName(room.getProduct().getName())
                            .lastMessage(lastContent)
                            .status(room.getStatus())
                            .unreadCount(0L)                    // 실습에서는 0 으로 고정, 추후 읽음 처리 연계
                            .build();
                })
                .collect(Collectors.toList());                  // List 로 변환
    }

    // 판매자 또는 관리자가 특정 방을 담당자로 배정하는 기능
    @Transactional
    public void assignRoom(Long roomId) {

        Users me = getCurrentUser();                            // 현재 사용자(판매자 또는 관리자)

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("상담방을 찾을 수 없습니다."));
        // roomId 로 상담방 조회

        List<ChatRoomParticipant> participants =
                participantRepository.findByRoomId(roomId);     // 이 방의 모든 참여자 조회

        boolean alreadyParticipant = participants.stream()      // 이미 이 방에 참여 중인지 확인
                .anyMatch(p -> p.getUser().getUser_id().equals(me.getUser_id()));

        if (!alreadyParticipant) {                              // 참여자가 아니라면 새로 참여자로 추가
            ChatRoomParticipant newParticipant = ChatRoomParticipant.builder()
                    .room(room)
                    .user(me)
                    .role("seller")                             // 여기서는 seller 로 가정, 필요 시 admin 으로 변경 가능
                    .build();
            participantRepository.save(newParticipant);
        }

        room.assign();                                          // 방 상태를 ASSIGNED 로 변경
        // @Transactional 덕분에 메서드 종료 시점에 변경 사항이 자동 반영됨
    }
}