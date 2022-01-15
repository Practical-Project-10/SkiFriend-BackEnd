package com.ppjt10.skifriend.dto.signupdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupKakaoDto {
    String token;
    Long userId;
}
