
package com.example.demo.dto.chat;

import com.example.demo.domain.chat.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
@Getter
@AllArgsConstructor
@Builder
public class ChatRoomSummaryResponse {

    private Long roomId;
    private String productName;  // ★ 프론트에서 사용하는 실제 값

    private String lastMessage;
    private String lastMessageTime;

    private Long unreadCount;

    public static ChatRoomSummaryResponse from(
            ChatRoom room,
            String lastMsg,
            Long unreadCount,
            String lastTime
    ) {
        return ChatRoomSummaryResponse.builder()
                .roomId(room.getId())
                .productName(room.getProduct().getName())   // ★ 이렇게 수정
                .lastMessage(lastMsg)
                .lastMessageTime(lastTime)
                .unreadCount(unreadCount)
                .build();
    }
}



//package com.example.demo.dto.chat;
//
//import com.example.demo.domain.chat.ChatRoom;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//
//@Getter
//@AllArgsConstructor
//@Builder
//public class ChatRoomSummaryResponse {
//
//    private Long roomId;
//    private Long productId;
//
//    private String lastMessage;
//    private String lastMessageTime;
//
//    private Long unreadCount;
//
//    public static ChatRoomSummaryResponse from(
//            ChatRoom room,
//            String lastMsg,
//            Long unreadCount,
//            String lastTime
//    ) {
//        return ChatRoomSummaryResponse.builder()
//                .roomId(room.getId())
//                .productId(room.getProductId())
//                .lastMessage(lastMsg)
//                .lastMessageTime(lastTime)
//                .unreadCount(unreadCount)
//                .build();
//    }
////}
