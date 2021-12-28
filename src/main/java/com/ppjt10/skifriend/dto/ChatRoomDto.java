package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ChatRoomDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseDto{
        private String roomName;
        private String roomId;
        private Long longRoomId;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRoomListResponseDto{
        private String roomName;
        private String roomId;
        private Long longRoomId;
        private String lastMsg;
        private String lastMsgTime;
        private String userProfile;
        private int notVerifiedMsgCnt;
    }
}
