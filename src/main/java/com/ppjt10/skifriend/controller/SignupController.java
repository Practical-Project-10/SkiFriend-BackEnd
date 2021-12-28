package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.certification.MessageService;
import com.ppjt10.skifriend.dto.SignupDto;
import com.ppjt10.skifriend.dto.UserDto;
import com.ppjt10.skifriend.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignupController {
    private final SignupService SignupService;
    private final MessageService messageService;

    //    // 회원가입
    //    @PostMapping("/user/signup")
    //    public void userSignup(@RequestPart("profileImg") MultipartFile profileImg,
    //                           @RequestPart("vacImg") MultipartFile vacImg,
    //                           @RequestPart("requestDto") UserDto.RequestDto requestDto
    //    ) throws IOException {
    //        userService.createUser(profileImg, vacImg, requestDto);
    //    }

    // 문자 SMS 인증
    @PostMapping("/user/sms")
    public ResponseEntity<String> sendSMS(@RequestBody SignupDto.PhoneNumDto phoneNumber) {
        return ResponseEntity.ok().body(messageService.sendSMS(phoneNumber.getPhoneNumber()));
    }

    // 인증번호 일치하는지 검증
    @PostMapping("/user/sms/check")
    public ResponseEntity<String> verifyRandNum(@RequestBody SignupDto.SmsCertificationDto requestDto) {
        return ResponseEntity.ok().body(messageService.verifySms(requestDto));
    }

    // 아이디 중복 체크
    @PostMapping("/user/signup/idcheck")
    public void checkIsUsername(@RequestBody UserDto.IdCheckDto idCheckDto){
        SignupService.checkIsId(idCheckDto.getUsername());
    }

    // 닉네임 중복 체크
    @PostMapping("/user/signup/nicknamecheck")
    public void checkIsNickname(@RequestBody UserDto.NicknameCheckDto nicknameCheckDto){
        SignupService.checkIsNickname(nicknameCheckDto.getNickname());
    }

    // 회원가입
    @PostMapping("/user/signup")
    public void signup(@RequestBody SignupDto.RequestDto requestDto){
        SignupService.signup(requestDto);
    }
}
