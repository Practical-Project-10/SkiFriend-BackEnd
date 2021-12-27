package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.List;

public class FreePostDto {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestDto{
        private String title;
        private String content;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseDto{
        private Long postId;
        private String nickname;
        private String title;
        private String content;
        private String image;
        private String createdAt;
        private List<LikesDto.ResponseDto> likesDtoList;
        private List<CommentDto.ResponseDto> commentDtoList;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HotResponseDto{
        private Long postId;
        private String title;
        private String createdAt;
        private int likeCnt;
        private int commentCnt;
    }

    //전체 게시글 내려주기
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AllResponseDto{
        private Long postId;
        private Long userId;
        private String nickname;
        private String createdAt;
        private String title;
        private int likeCnt;
        private int commentCnt;
    }
}
