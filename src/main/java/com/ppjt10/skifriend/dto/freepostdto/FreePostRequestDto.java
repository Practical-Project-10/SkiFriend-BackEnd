package com.ppjt10.skifriend.dto.freepostdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FreePostRequestDto{
    private String title;
    private String content;
}
