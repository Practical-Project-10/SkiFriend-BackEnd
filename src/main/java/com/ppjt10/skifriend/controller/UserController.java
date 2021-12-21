package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.UserDto;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.MessageService;
import com.ppjt10.skifriend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MessageService messageService;

    @GetMapping("/check/sendsms")
    public ResponseEntity<String> sendSMS(@RequestBody UserDto.requestForSMS phoneNumber){
        return ResponseEntity.ok().body(messageService.sendSMS(phoneNumber.getPhoneNumber()));
    }

    @PostMapping("/user/signup")
    public void userSignup(@RequestPart("profileImg") MultipartFile profileImg,
                           @RequestPart("vacImg") MultipartFile vacImg,
                           @RequestPart("requestDto") UserDto.RequestDto requestDto
                           ) throws IOException {
        userService.createUser(profileImg, vacImg, requestDto);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserDto.ResponseDto> getUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok().body(userService.getUserInfo(userDetails.getUser().getId()));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<UserDto.ResponseDto> updateUserInfo(@RequestPart ("profileImg") MultipartFile profileImg,
                                                              @RequestPart("vacImg") MultipartFile vacImg,
                                                              @RequestPart("requestDto") UserDto.updateRequestDto requestDto,
                                                              @AuthenticationPrincipal UserDetailsImpl userDetails){

        return ResponseEntity.ok().body(userService.updateUserInfo(profileImg, vacImg, requestDto, userDetails.getUser().getId()));
    }

    @DeleteMapping("/user/{userId}")
    public void deleteUser(@AuthenticationPrincipal UserDetailsImpl userDetails){
        userService.deleteUser(userDetails.getUser().getId());
    }
}
