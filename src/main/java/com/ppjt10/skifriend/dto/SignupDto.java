package com.ppjt10.skifriend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SignupDto {
    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PhoneNumDto{
        private String phoneNumber;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SmsCertificationDto{
        private String phoneNumber;
        private String randomNumber;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestDto{
        private String username;
        private String password;
        private String phoneNum;
        private String nickname;
    }
}
