package com.ppjt10.skifriend.dto.shortscommentdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortsCommentRequestDto {
    private String content;
}
