package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

public class UserDto {
    @Builder
    @Getter
    @AllArgsConstructor
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
    public static class ResponseDto{
        private String username;
        private String password;
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
    public static class requestForSMS{
        private String phoneNumber;
    }
}
