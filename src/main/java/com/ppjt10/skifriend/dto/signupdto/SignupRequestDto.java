package com.ppjt10.skifriend.dto.signupdto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto{
    private String username;
    private String password;
    private String phoneNum;
    private String nickname;
}