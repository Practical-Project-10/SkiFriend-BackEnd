package com.ppjt10.skifriend.dto.shortsdto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortsMyResponseDto {
    private String title;
    private String videoPath;
}
