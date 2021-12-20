package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

public class CarpoolDto {
    @Builder
    @Getter
    @AllArgsConstructor
    public static class RequestDto{
        private String carpoolType;
        private String startLocation;
        private String endLocation;
        private String date;
        private String time;
        private int price;
        private int memberNum;
        private String notice;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class ResponseDto{
        private Long userId;
        private Long postId;
        private String carpoolType;
        private String startLocation;
        private String endLocation;
        private String date;
        private String time;
        private int price;
        private int memberNum;
        private String notice;
    }
}
