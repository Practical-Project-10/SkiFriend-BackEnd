package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.ChatRoomDto;

import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }

    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }

    //region 내가 참여한 모든 채팅방 목록 조회
    @GetMapping("/rooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoomDto.ResponseDto>> room(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        List<ChatRoomDto.ResponseDto> responseDtos = chatRoomService.findAllRoom(userDetails);

        return ResponseEntity.ok().body(responseDtos);
    }
    //endregion


    //region 채팅방 생성
    @PostMapping("/room/{carpoolId}")
    @ResponseBody
    public ResponseEntity<ChatRoomDto.ResponseDto> createRoom(
            @PathVariable Long carpoolId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        ChatRoomDto.ResponseDto responseDto = chatRoomService.createChatRoom(carpoolId, userDetails);

        return ResponseEntity.ok().body(responseDto);
    }
    //endregion

    //region 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ResponseEntity<ChatRoomDto.ResponseDto> roomInfo(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return chatRoomService.findRoomById(roomId, userDetails);
    }
    //endregion



}