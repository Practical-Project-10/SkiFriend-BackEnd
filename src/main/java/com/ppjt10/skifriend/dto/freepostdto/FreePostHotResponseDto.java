package com.ppjt10.skifriend.dto.freepostdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FreePostHotResponseDto{
    private Long postId;
    private String title;
    private String skiResort;
    private String createdAt;
    private int likeCnt;
    private int commentCnt;
}