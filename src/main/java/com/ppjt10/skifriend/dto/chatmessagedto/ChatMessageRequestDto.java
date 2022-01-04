package com.ppjt10.skifriend.dto.chatmessagedto;

import com.ppjt10.skifriend.entity.ChatMessage;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequestDto {
    private ChatMessage.MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private long userCount;
}
