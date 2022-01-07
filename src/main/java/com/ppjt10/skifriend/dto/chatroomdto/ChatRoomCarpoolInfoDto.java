package com.ppjt10.skifriend.dto.chatroomdto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomCarpoolInfoDto {
    private String title;
    private String startLocation;
    private String endLocation;
    private String date;
    private String time;
    private int price;
    private String memberNum;
    private String notice;
}
