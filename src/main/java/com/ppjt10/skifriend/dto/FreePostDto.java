package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

public class FreePostDto {
    @Builder
    @Getter
    @AllArgsConstructor
    public static class RequestDto{
        private String title;
        private String content;
//        private String image; // Formdata고려
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class ResponseDto{
        private Long postId;
        private String nickname;
        private String title;
        private String content;
        private String image;
        private String createdAt;
        private List<LikesDto> likesDtoList;
        private List<CommentDto> commentDtoList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class ReortTabDto{
        private Long postId;
        private String nickname;
        private String title;
        private String content;
        private String image;
        private String createdAt;
        private int likeCnt;
        private int commentCnt;
    }
}
