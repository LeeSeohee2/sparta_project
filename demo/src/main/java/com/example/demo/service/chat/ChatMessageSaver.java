package com.example.demo.service.chat;

import com.example.demo.domain.chat.ChatMessage;
import com.example.demo.domain.chat.ChatRoom;
import com.example.demo.domain.user.Users;
import com.example.demo.dto.chat.ChatMessageRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.chat.ChatMessageRepository;
import com.example.demo.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageSaver {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatMessage save(ChatMessageRequest request) {

        ChatRoom room = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        Users sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("보낸 사람 없음"));

        ChatMessage message = ChatMessage.builder()
                .room(room)
                .sender(sender)
                .content(request.getContent())
                .type(request.getType())
                .build();

        return chatMessageRepository.save(message);
    }
}