package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.UserDto;
import com.ppjt10.skifriend.service.MessageService;
import com.ppjt10.skifriend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final MessageService messageService;

    @PostMapping("/user/signup")
    public void userSignup(@ModelAttribute UserDto.RequestDto requestDto){
        userService.createUser(requestDto);
    }

    @GetMapping("/check/sendsms")
    public ResponseEntity<String> sendSMS(@RequestBody UserDto.requestForSMS phoneNumber){
        // return ResponseEntity.ok().body(messageService.sendSMS(phoneNumber.getPhoneNumber()));
        return ResponseEntity.ok().body(messageService.createRandomNumber());
    }
}
