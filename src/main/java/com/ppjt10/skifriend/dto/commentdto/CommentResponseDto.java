package com.ppjt10.skifriend.dto.commentdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto{
    private Long userId;
    private Long commentId;
    private String userProfile;
    private String nickname;
    private String content;
    private String createdAt;
}