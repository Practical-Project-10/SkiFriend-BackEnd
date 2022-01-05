package com.ppjt10.skifriend.dto.signupdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupPhoneNumDto {
    private String phoneNumber;
}