package com.ppjt10.skifriend.dto.freepostdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 자유게시판 전체조회, 작성 ,수정
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FreePostResponseDto{
    private Long postId;
    private Long userId;
    private String nickname;
    private String createdAt;
    private String title;
    private int likeCnt;
    private int commentCnt;
}
