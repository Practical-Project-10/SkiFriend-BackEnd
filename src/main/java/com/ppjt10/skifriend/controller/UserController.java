package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.CarpoolDto;
import com.ppjt10.skifriend.dto.UserDto;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.certification.MessageService;
import com.ppjt10.skifriend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MessageService messageService;

    // 문자 SMS 인증
    @GetMapping("/user/sms")
    public ResponseEntity<String> sendSMS(@RequestBody UserDto.phoneNumDto phoneNumber) {
        return ResponseEntity.ok().body(messageService.sendSMS(phoneNumber.getPhoneNumber()));
    }

    // 인증번호 일치하는지 검증
    @PostMapping("/user/sms/check")
    public ResponseEntity<String> verifyRandNum(@RequestBody UserDto.SmsCertificationDto requestDto) {
        return ResponseEntity.ok().body(messageService.verifySms(requestDto));
    }


    // 유저 핸드폰 번호 공개하기
    @GetMapping("/user/Info/phoneNum")
    public ResponseEntity<UserDto.phoneNumDto> getPhoneNum(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(userService.getPhoneNum(userDetails.getUser().getId()));
    }

    // 회원가입
    @PostMapping("/user/signup")
    public void userSignup(@RequestPart("profileImg") MultipartFile profileImg,
                           @RequestPart("vacImg") MultipartFile vacImg,
                           @RequestPart("requestDto") UserDto.RequestDto requestDto
    ) throws IOException {
        userService.createUser(profileImg, vacImg, requestDto);
    }

    // 유저 정보 조회하기
    @GetMapping("/user/Info")
    public ResponseEntity<UserDto.ResponseDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(userService.getUserInfo(userDetails.getUser().getId()));
    }

    // 유저 정보 수정하기
    @PutMapping("/user/Info")
    public ResponseEntity<UserDto.ResponseDto> updateUserInfo(@RequestPart("profileImg") MultipartFile profileImg,
                                                              @RequestPart("vacImg") MultipartFile vacImg,
                                                              @RequestPart("requestDto") UserDto.updateRequestDto requestDto,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok().body(userService.updateUserInfo(profileImg, vacImg, requestDto, userDetails.getUser().getId()));
    }

    // 유저 삭제하기
    @DeleteMapping("/user/Info")
    public void deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.deleteUser(userDetails.getUser().getId());
    }

    // 내가 쓴 카풀 게시물 불러오기
    @GetMapping("/user/info/carpool")
    public ResponseEntity<List<CarpoolDto.ResponseDto>> myCarpools(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok().body(userService.findMyCarpools(userDetails.getUser()));
    }
}
