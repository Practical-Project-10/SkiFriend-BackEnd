package com.ppjt10.skifriend.validator;

import lombok.RequiredArgsConstructor;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UserInfoValidator {
    public static void validateUserInfoInput(String username, String nickname, String phoneNum) {
        String patternUsername = "(?=.*[a-zA-Z])(?=.*[0-9])[^@$!%*#?&].{4,}$";
        String patternPhoneNum = "(?=.*[0-9]).{11}$";

        // 아이디 형식 확인
        if (username == null || !Pattern.matches(patternUsername, username)) {
            throw new IllegalArgumentException("영문, 숫자 5자리 이상, 특수 문자 사용 불가 합니다.");
        }

        // 닉네임 형식 확인
        if (nickname == null || nickname.length() > 7) {
            throw new IllegalArgumentException("닉네임을 7자리 이하로 입력해주세요.");
        }

        // 휴대전화번호 형식 확인
        if (phoneNum == null || !Pattern.matches(patternPhoneNum, phoneNum)) {
            throw new IllegalArgumentException("휴대전화 번호를 정확히 입력해주세요.");
        }
    }

    public static void validatePassword(String password){
        String patternPassword = "(?=.*[a-z])(?=.*\\d)[a-z\\d]{7,}";

        // 비밀번호 형식 확인
        if (password == null || !Pattern.matches(patternPassword, password)) {
            throw new IllegalArgumentException("영소문자와 숫자 반드시 포함, 최소 8자 이상이어야 합니다.");
        }
    }
}
