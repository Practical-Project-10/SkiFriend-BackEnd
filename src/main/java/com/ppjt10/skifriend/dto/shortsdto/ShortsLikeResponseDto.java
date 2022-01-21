package com.ppjt10.skifriend.dto.shortsdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShortsLikeResponseDto {
    private Long userId;
}
