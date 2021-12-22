package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class BoardListResponseDto {
    private List<CarpoolDto.ResponseDto> carpoolResponseDto;
    private List<FreePostDto.AllResponseDto> freePostAllResponseDto;
}

