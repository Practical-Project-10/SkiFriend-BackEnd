package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.CarpoolDto;
import com.ppjt10.skifriend.dto.SignupDto;
import com.ppjt10.skifriend.dto.UserDto;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 유저 핸드폰 번호 공개하기
    @GetMapping("/user/Info/phoneNum")
    public ResponseEntity<SignupDto.PhoneNumDto> getPhoneNum(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(userService.getPhoneNum(userDetails.getUser().getId()));
    }

    // 유저 프로필 작성
    @PostMapping("/user/profile")
    public ResponseEntity<UserDto.ResponseDto> writeUserProfile(
            @RequestPart("profileImg") MultipartFile profileImg,
            @RequestPart("vacImg") MultipartFile vacImg,
            @RequestPart("requestDto") UserDto.ProfileRequestDto requestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok().body(userService.writeUserProfile(profileImg, vacImg, requestDto, userDetails.getUser().getId()));
    }

    // 유저 정보 조회하기
    @GetMapping("/user/Info")
    public ResponseEntity<UserDto.ResponseDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok().body(userService.getUserInfo(userDetails.getUser().getId()));
    }

    // 비밀번호 수정하기
    @PutMapping("/user/Info/password")
    public void updatePassword(
            @RequestBody UserDto.PasswordDto passwordDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        userService.updatePassword(passwordDto, userDetails.getUser().getId());
    }

    // 유저 정보 수정하기
    @PutMapping("/user/Info")
    public ResponseEntity<UserDto.ResponseDto> updateUserInfo(@RequestPart("profileImg") MultipartFile profileImg,
                                                              @RequestPart("vacImg") MultipartFile vacImg,
                                                              @RequestPart("requestDto") UserDto.UpdateRequestDto requestDto,
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
