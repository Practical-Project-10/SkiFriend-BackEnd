package com.ppjt10.skifriend.dto.carpooldto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//카풀 게시물 작성,수정
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CarpoolRequestDto {
    private String carpoolType;
    private String title;
    private String startLocation;
    private String endLocation;
    private String date;
    private String time;
    private int price;
    private String memberNum;
    private String notice;
}
