package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentDto {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestDto{
        private String content;
    }


}
