package com.ppjt10.skifriend.dto.userdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequestDto{
//    private String gender;
//    private String ageRange;
    private String career;
    private String selfIntro;
}