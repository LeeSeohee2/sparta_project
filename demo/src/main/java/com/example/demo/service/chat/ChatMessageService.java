package com.example.demo.service.chat;

import com.example.demo.domain.chat.ChatMessage;
import com.example.demo.domain.chat.ChatRoom;
import com.example.demo.domain.read.ReadState;
import com.example.demo.domain.user.Users;
import com.example.demo.dto.chat.ChatMessagePageResponse;
import com.example.demo.dto.chat.ChatMessageRequest;
import com.example.demo.dto.chat.ChatMessageResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.chat.ChatMessageRepository;
import com.example.demo.repository.chat.ChatRoomRepository;
import com.example.demo.repository.read.ReadStateRepository;
import com.example.demo.websocket.RedisPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ReadStateRepository readStateRepository;
    private final RedisPublisher redisPublisher;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    // ================================
    // 1) 메시지 조회 (커서 기반)
    // ================================
    @Transactional(readOnly = true)
    public ChatMessagePageResponse getMessages(Long roomId,
                                               Long cursor,
                                               Long currentUserId) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        List<ChatMessage> entities;

        if (cursor == null) {
            entities = chatMessageRepository.findTop50ByRoomOrderByIdDesc(room);
        } else {
            entities = chatMessageRepository.findTop50ByRoomAndIdLessThanOrderByIdDesc(room, cursor);
        }

        Collections.reverse(entities);

        List<ChatMessageResponse> dtos = entities.stream()
                .map(m -> toResponse(m, currentUserId))
                .collect(Collectors.toList());

        Long nextCursor = null;
        if (!entities.isEmpty()) {
            Long minId = entities.get(0).getId();
            boolean hasMore = chatMessageRepository.existsByRoomAndIdLessThan(room, minId);
            if (hasMore) nextCursor = minId;
        }


        // ⭐⭐⭐ [추가됨] 현재 사용자의 unreadCount 계산 ⭐⭐⭐
        Long lastReadId = readStateRepository.findByRoomIdAndUserId(roomId, currentUserId)
                .map(ReadState::getLastReadMessageId)
                .orElse(0L);

        long unreadCount = chatMessageRepository.countByRoomAndIdGreaterThan(room, lastReadId);

        // ⭐⭐⭐ PageResponse 에 unreadCount 추가하려면 DTO 수정 필요 ⭐⭐⭐
        return ChatMessagePageResponse.builder()
                .messages(dtos)
                .nextCursor(nextCursor)
                .unreadCount(unreadCount)        // ← 추가됨
                .build();
    }


    // ================================
    // DTO 변환 메서드
    // ================================
    private ChatMessageResponse toResponse(ChatMessage message,
                                           Long currentUserId) {

        boolean mine = message.getSender().getUserId().equals(currentUserId);

        return ChatMessageResponse.builder()
                .messageId(message.getId())
                .roomId(message.getRoom().getId())
                .senderId(message.getSender().getUserId())
                .senderName(message.getSender().getName())
                .content(message.getContent())
                .type(message.getType())
                .createdAt(message.getCreatedAt().format(DATE_FORMATTER))
                .mine(mine)
                .build();
    }


    // ================================
    // 2) 메시지 저장 + Redis 발행
    // ================================
    @Transactional
    public ChatMessageResponse saveAndPublish(ChatMessageRequest request) {

        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        Users sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ChatMessage message = ChatMessage.builder()
                .room(room)
                .sender(sender)
                .content(request.getContent())
                .type(request.getType())
                .build();

        ChatMessage saved = chatMessageRepository.save(message);


        // ⭐⭐⭐ [추가됨] 새 메시지가 오면 “상대방 unreadCount 증가 효과”
        updateUnreadForOtherParticipants(room.getId(), sender.getUserId(), saved.getId());


        ChatMessageResponse response = ChatMessageResponse.builder()
                .messageId(saved.getId())
                .roomId(room.getId())
                .senderId(sender.getUserId())
                .senderName(sender.getName())
                .content(saved.getContent())
                .type(saved.getType())
                .createdAt(saved.getCreatedAt().format(DATE_FORMATTER))
                .mine(false)
                .build();

        redisPublisher.publish(response);

        return response;
    }


    // ⭐⭐⭐ 신규 메시지 도착 → 상대방 unreadCount 증가 처리 ⭐⭐⭐
    private void updateUnreadForOtherParticipants(Long roomId, Long senderId, Long newMessageId) {

        // 자신(sender)의 ReadState 는 건드리지 않음
        List<ReadState> states = readStateRepository.findByRoomId(roomId);

        for (ReadState state : states) {
            if (!state.getUserId().equals(senderId)) {
                // 상대방의 lastReadMessageId 는 그대로 → unreadCount 자연 증가
                // 저장 필요 없음 (ReadState 자체는 그대로 유지)
            }
        }
    }


    // ================================
    // 3) 읽음 처리 업데이트
    // ================================
    @Transactional
    public void updateReadState(Long roomId, Long userId, Long lastReadMessageId) {

        ReadState state = readStateRepository
                .findByRoomIdAndUserId(roomId, userId)
                .orElseGet(() -> new ReadState(roomId, userId, 0L));

        // ⭐⭐⭐ 사용자가 메시지를 읽으면 마지막 읽은 위치 업데이트! ⭐⭐⭐
        state.updateLastReadMessage(lastReadMessageId);

        readStateRepository.save(state);
    }
}
