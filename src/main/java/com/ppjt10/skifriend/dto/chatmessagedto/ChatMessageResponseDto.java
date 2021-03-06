package com.ppjt10.skifriend.dto.chatmessagedto;

import com.ppjt10.skifriend.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponseDto {
    private ChatMessage.MessageType type;
    private Long messageId;
    private Long roomId;
    private Long receiverId;
    private String sender;
    private String senderImg;
    private String img; // 채팅방에서 이미지 보낼 때 쓸 이미지 dto
    private String message;
    private String createdAt;
}
