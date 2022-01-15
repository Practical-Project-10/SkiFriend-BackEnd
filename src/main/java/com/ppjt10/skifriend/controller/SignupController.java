package com.ppjt10.skifriend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ppjt10.skifriend.certification.MessageService;
import com.ppjt10.skifriend.dto.signupdto.*;
import com.ppjt10.skifriend.service.KakaoUserService;
import com.ppjt10.skifriend.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class SignupController {
    private final SignupService SignupService;
    private final MessageService messageService;
    private final KakaoUserService kakaoUserService;
    private final String AUTH_HEADER = "Authorization";

    // 문자 SMS 인증
    @PostMapping("/user/sms")
    public ResponseEntity<String> getSmsCertification(@RequestBody SignupPhoneNumDto phoneNumber) {
        return ResponseEntity.ok().body(messageService.getSmsRedisRepository(phoneNumber));
    }

    // 인증번호 일치하는지 검증
    @PostMapping("/user/sms/check")
    public ResponseEntity<String> checkCertificationNum(@RequestBody SignupSmsCertificationDto requestDto) {
        return ResponseEntity.ok().body(messageService.checkCertificationNum(requestDto));
    }

    // 아이디 중복 체크
    @PostMapping("/user/signup/idcheck")
    public void checkId(@RequestBody SignupIdCheckDto idCheckDto){
        SignupService.checkId(idCheckDto.getUsername());
    }

    // 닉네임 중복 체크
    @PostMapping("/user/signup/nicknamecheck")
    public void checkNickname(@RequestBody SignupNicknameCheckDto nicknameCheckDto){
        SignupService.checkNickname(nicknameCheckDto.getNickname());
    }

    // 회원가입
    @PostMapping("/user/signup")
    public void signup(@RequestBody SignupRequestDto requestDto){
        SignupService.signup(requestDto);
    }

    // 카카오 회원가입
    @GetMapping("/user/kakao/callback")
    public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // authorizedCode: 카카오 서버로부터 받은 인가 코드
        String token = kakaoUserService.kakaoLogin(code);
        response.addHeader(AUTH_HEADER, token);
    }
}
