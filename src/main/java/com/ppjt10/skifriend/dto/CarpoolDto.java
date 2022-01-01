package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class CarpoolDto {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestDto{
        private String carpoolType;
        private String title;
        private String startLocation;
        private String endLocation;
        private String date;
        private String time;
        private int price;
        private int memberNum;
        private String notice;
    }

    //조회용
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseDto{
        private Long postId;
        private Long userId;
        private String nickname;
        private String createdAt;
        private String carpoolType;
        private String title;
        private String startLocation;
        private String endLocation;
        private String skiResort;
        private String date;
        private String time;
        private int price;
        private int memberNum;
        private String notice;
        private boolean status;
    }

    //필터링 정보
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryRequestDto{
        private String carpoolType;
        private int memberNum;
        private String startLocation;
        private String endLocation;
        private String date;
    }

}
