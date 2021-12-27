package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRoomDto {
    //    Carpool carpool, List<ChatMessage> chatMessageList, Long senderId, List<ChatUserInfo> chatUserInfoList
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseDto{
        private String roomName;
        private String roomId;
    }
}
