package com.ppjt10.skifriend.dto.userdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 비밀번호 수정
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordUpdateDto{
    private String password;
    private String newPassword;
}