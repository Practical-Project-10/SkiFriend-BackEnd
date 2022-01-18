package com.ppjt10.skifriend.dto.signupdto;

import com.ppjt10.skifriend.dto.userdto.UserLoginResponseDto;
import com.ppjt10.skifriend.dto.userdto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupSocialDto {
    String token;
    Long userId;
    UserResponseDto userResponseDto;
}

