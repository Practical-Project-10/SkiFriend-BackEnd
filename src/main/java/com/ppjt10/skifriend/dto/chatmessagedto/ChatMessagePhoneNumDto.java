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
public class ChatMessagePhoneNumDto {
    private ChatMessage.MessageType type;
    private Long roomId;
    private String message;
    private String sender;

}
