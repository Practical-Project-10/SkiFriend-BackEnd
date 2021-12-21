package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserDto {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestDto{
        private String username;
        private String password;
        private String phoneNum;
        private String nickname;
        private String gender;
        private String ageRange;
        private String career;
        private String selfIntro;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class updateRequestDto{
        private String password;
        private String nickname;
        private String career;
        private String selfIntro;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResponseDto{
        private String username;
        private String phoneNum;
        private String nickname;
        private String profileImg;
        private String vacImg;
        private String gender;
        private String ageRange;
        private String career;
        private String selfIntro;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class phoneNumDto{
        private String phoneNumber;
    }
}
