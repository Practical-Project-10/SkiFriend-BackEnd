package com.ppjt10.skifriend.dto;


import com.ppjt10.skifriend.entity.ChatMessage;
import com.ppjt10.skifriend.entity.User;
import lombok.*;

import java.util.List;


@Getter
public class ChatMessageDto {
    //일단 두개로 만들어줌 나중에 수정 예정
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestDto{
        private ChatMessage.MessageType type;
        private String roomId;
        private String sender;
        private String message;
        private long userCount;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseDto{
        private ChatMessage.MessageType type;
        private String roomId;
        private String sender;
        private String message;
        private String createdAt;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InChatRoomResponseDto{
        private String  roomId;
        private String roomName;
        private List<ChatMessageDto.ResponseDto> roomContents;
    }

}
