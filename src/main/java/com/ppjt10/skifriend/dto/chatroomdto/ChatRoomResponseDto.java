package com.ppjt10.skifriend.dto.chatroomdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomResponseDto{
    private String roomName;
    private Long roomId;
    private Long longRoomId;
}
