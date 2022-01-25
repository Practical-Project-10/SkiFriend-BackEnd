package com.ppjt10.skifriend.dto.shortsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortsCommentResponseDto {
    private Long userId;
    private Long shortsCommentId;
    private String userImg;
    private String nickname;
    private String content;
    private String createdAt;
}
