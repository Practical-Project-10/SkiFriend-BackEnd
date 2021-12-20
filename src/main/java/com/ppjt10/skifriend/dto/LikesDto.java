package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

public class LikesDto {
    @Builder
    @Getter
    @AllArgsConstructor
    public static class ResponseDto{
        private Long userId;
    }
}
