package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShortsResponseDto {
    private Long shortsId;
    private Long userId;
    private String nickname;
    private String profileImg;
    private String videoPath;
    private String title;
    private int shortsLikeCnt;
    private int shortsCommentCnt;
}
