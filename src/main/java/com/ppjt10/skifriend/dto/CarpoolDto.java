package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

public class CarpoolDto {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
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
    @NoArgsConstructor
    public static class ResponseDto{
        private Long postId;
        private Long userId;
        private String nickname;
        private String createdAt;
        private String carpoolType;
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
}
