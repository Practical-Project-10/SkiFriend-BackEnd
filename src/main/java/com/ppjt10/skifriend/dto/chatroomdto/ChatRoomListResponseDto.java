package com.ppjt10.skifriend.dto.chatroomdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomListResponseDto{
    private String roomName;
    private String roomId;
    private Long longRoomId;
    private String lastMsg;
    private String lastMsgTime;
    private String userProfile;
    private int notVerifiedMsgCnt;
}