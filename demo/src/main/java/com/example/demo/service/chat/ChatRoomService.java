package com.example.demo.service.chat;

import com.example.demo.domain.chat.ChatMessage;
import com.example.demo.domain.chat.ChatRoom;
import com.example.demo.domain.chat.ChatRoomParticipant;
import com.example.demo.domain.product.Product;
import com.example.demo.domain.read.ReadState;
import com.example.demo.domain.user.Users;
import com.example.demo.dto.chat.ChatRoomSummaryResponse;
import com.example.demo.dto.chat.InquiryCreateRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.chat.ChatMessageRepository;
import com.example.demo.repository.chat.ChatRoomParticipantRepository;
import com.example.demo.repository.chat.ChatRoomRepository;
import com.example.demo.repository.product.ProductRepository;

// ⭐ 반드시 추가해야 됨!
import com.example.demo.repository.read.ReadStateRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ProductRepository productRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomParticipantRepository participantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    // ⭐ 추가
    private final ReadStateRepository readStateRepository;

    private Users getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public ChatRoomSummaryResponse createInquiry(InquiryCreateRequest request) {

        Users buyer = getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Users seller = product.getSeller();
        if (seller == null) {
            throw new IllegalStateException("해당 상품에 판매자가 지정되지 않았습니다.");
        }

        ChatRoom room = ChatRoom.builder()
                .product(product)
                .status("OPEN")
                .build();
        chatRoomRepository.save(room);

        // 참여자 등록 (구매자, 판매자)
        participantRepository.save(
                ChatRoomParticipant.builder().room(room).user(buyer).role("buyer").build()
        );

        participantRepository.save(
                ChatRoomParticipant.builder().room(room).user(seller).role("seller").build()
        );

        // 첫 메시지 저장
        ChatMessage firstMessage = ChatMessage.builder()
                .room(room)
                .sender(buyer)
                .type("TEXT")
                .content(request.getContent())
                .build();
        chatMessageRepository.save(firstMessage);

        return ChatRoomSummaryResponse.builder()
                .roomId(room.getId())
                .productName(product.getName())
                .lastMessage(firstMessage.getContent())
                .unreadCount(0L)
                .build();
    }

    /** ========================
     *  내가 참여한 방 목록 조회
     * ======================== */
    @Transactional(readOnly = true)
    public List<ChatRoomSummaryResponse> getMyRooms() {

        Users me = getCurrentUser();

        List<ChatRoomParticipant> myRooms =
                participantRepository.findByUser(me);

        return myRooms.stream()
                .map(participant -> {

                    ChatRoom room = participant.getRoom();

                    // 🔹 최근 메시지
                    ChatMessage lastMsg = chatMessageRepository
                            .findTopByRoomOrderByCreatedAtDesc(room)
                            .orElse(null);

                    String lastContent = (lastMsg != null) ? lastMsg.getContent() : "";
                    String lastTime = (lastMsg != null) ? lastMsg.getCreatedAt().toString() : "";

                    // 🔹 🔥 ReadState 기반 unreadCount 계산
                    long unreadCount = 0;

                    // 1) 내 읽음 정보 조회
                    Long lastReadId = readStateRepository.findByRoomIdAndUserId(
                            room.getId(), me.getUserId()
                    ).map(ReadState::getLastReadMessageId).orElse(0L);

                    // 2) 아직 내가 안 읽은 메시지 개수
                    if (lastMsg != null) {
                        unreadCount = chatMessageRepository
                                .countByRoomAndIdGreaterThan(room, lastReadId);
                    }

                    return ChatRoomSummaryResponse.from(
                            room,
                            lastContent,
                            unreadCount,
                            lastTime
                    );
                })
                .collect(Collectors.toList());
    }


    /** ========================
     *  전체 unreadCount 반환
     * ======================== */
    @Transactional(readOnly = true)
    public Long getTotalUnreadForCurrentUser() {

        Users me = getCurrentUser();

        List<ChatRoomParticipant> myRooms =
                participantRepository.findByUser(me);

        long total = 0L;

        for (ChatRoomParticipant part : myRooms) {

            ChatRoom room = part.getRoom();

            Long lastRead = readStateRepository
                    .findByRoomIdAndUserId(room.getId(), me.getUserId())
                    .map(ReadState::getLastReadMessageId)
                    .orElse(0L);

            Long unread = chatMessageRepository
                    .countByRoomAndIdGreaterThan(room, lastRead);

            total += unread;
        }

        return total;
    }


    @Transactional
    public void assignRoom(Long roomId) {

        Users me = getCurrentUser();

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("상담방을 찾을 수 없습니다."));

        boolean already = participantRepository
                .findByRoomId(roomId)
                .stream()
                .anyMatch(p -> p.getUser().getUserId().equals(me.getUserId()));

        if (!already) {
            participantRepository.save(
                    ChatRoomParticipant.builder()
                            .room(room)
                            .user(me)
                            .role("seller")
                            .build()
            );
        }

        room.assign();
    }
}
