package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.carpooldto.CarpoolResponseDto;
import com.ppjt10.skifriend.dto.signupdto.SignupPhoneNumDto;
import com.ppjt10.skifriend.dto.userdto.*;
import com.ppjt10.skifriend.entity.User;
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
    @GetMapping("/user/info/phoneNum")
    public ResponseEntity<SignupPhoneNumDto> getPhoneNum(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(userService.getPhoneNum(user));
    }

    // 유저 프로필 작성
//    @PostMapping("/user/profile")
//    public ResponseEntity<UserResponseDto> createUserProfile(@RequestPart(value = "profileImg", required = false) MultipartFile profileImg,
//                                                             @RequestPart(value = "requestDto") UserProfileRequestDto requestDto,
//                                                             @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) {
//        User user = userDetails.getUser();
//        return ResponseEntity.ok().body(userService.createUserProfile(profileImg, requestDto, user));
//    }

    // 유저 정보 조회하기
    @GetMapping("/user/info")
    public ResponseEntity<UserResponseDto> getUserProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(userService.getUserProfile(user));
    }

    // 유저 정보 수정하기
    @PutMapping("/user/info")
    public ResponseEntity<UserResponseDto> updateUserProfile(@RequestPart(value = "profileImg", required = false) MultipartFile profileImg,
                                                             @RequestPart(value = "requestDto") UserProfileUpdateDto requestDto,
                                                             @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        UserResponseDto userResponseDto = userService.updateUserProfile(profileImg, requestDto, user);
        return ResponseEntity.ok().body(userResponseDto);
    }

    // 유저 삭제하기(회원 탈퇴)
    @DeleteMapping("/user/info")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(userService.deleteUser(user));
    }

    // 비밀번호 수정하기
//    @PutMapping("/user/info/password")
//    public void updatePassword(@RequestBody UserPasswordUpdateDto passwordDto,
//                               @AuthenticationPrincipal UserDetailsImpl userDetails
//    ) {
//        User user = userDetails.getUser();
//        userService.updatePassword(passwordDto, user);
//    }

    // 내가 쓴 카풀 게시물 불러오기
    @GetMapping("/user/info/carpool")
    public ResponseEntity<List<CarpoolResponseDto>> getMyCarpools(@AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(userService.getMyCarpools(user));
    }

    // 채팅 방에서 상대방 프로필 조회하기
    @GetMapping("/user/introduction/{longRoomId}")
    public ResponseEntity<UserProfileOtherDto> getOtherProfile(@PathVariable Long longRoomId,
                                                               @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(userService.getOtherProfile(longRoomId, user));
    }
}
