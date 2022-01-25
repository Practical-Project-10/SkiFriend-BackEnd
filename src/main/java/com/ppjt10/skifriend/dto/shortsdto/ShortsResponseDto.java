package com.ppjt10.skifriend.dto.shortsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private String thumbNailPath;
    private String title;
    private List<ShortsLikeResponseDto> shortsLikeResponseDtoList;
    private int shortsLikeCnt;
    private int shortsCommentCnt;
}
