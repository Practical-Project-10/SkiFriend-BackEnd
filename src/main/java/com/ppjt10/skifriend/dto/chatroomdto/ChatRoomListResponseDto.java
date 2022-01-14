package com.ppjt10.skifriend.dto.chatroomdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomListResponseDto implements Comparable<ChatRoomListResponseDto>{
    private String roomName;
    private Long roomId;
    private Long longRoomId;
    private String lastMsg;
    private String lastMsgTime;
    private String userProfile;
    private int notVerifiedMsgCnt;




    @Override
    public int compareTo(ChatRoomListResponseDto chatRoomListResponseDto) {
        return chatRoomListResponseDto.getLastMsgTime().compareTo(this.lastMsgTime);
    }
}