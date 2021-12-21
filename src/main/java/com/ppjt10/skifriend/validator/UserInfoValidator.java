package com.ppjt10.skifriend.validator;

import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UserInfoValidator {
    public static void validateUserInfoInput(String username, String nickname, String password) {
        String patternUsername = "(?=.*[a-zA-Z])(?=.*[0-9])[^@$!%*#?&].{5,}$";
        String patternPassword = "[A-Za-z\\d$@$!%*#?&]{8,}$";

        if (username == null || !Pattern.matches(patternUsername, username)) {
            throw new IllegalArgumentException("영문, 숫자 5자리 이상, 특수 문자 사용 불가 합니다.");
        }

        // 닉네임 형식 확인
        if (nickname == null) {
            throw new IllegalArgumentException("닉네임 값이 없습니다.");
        }

        // 비밀번호 형식 확인
        if (password == null || !Pattern.matches(patternPassword, password)) {
            throw new IllegalArgumentException("특수문자 영어 숫자 포함, 최소 8자 이상이어야 합니다.");
        }
    }
}
