package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.ChatRoomDto;

import com.ppjt10.skifriend.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    //region 모든 채팅방 목록 조회
    @GetMapping("/rooms")
    @ResponseBody
    public ResponseEntity<List<ChatRoomDto.ResponseDto>> room() {
        return chatRoomService.findAllRoom();
    }
    //endregion


    //region 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public ResponseEntity<ChatRoomDto.ResponseDto> createRoom(@RequestParam String name) {
        return chatRoomService.createChatRoom(name);
    }
    //endregion

    //region 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ResponseEntity<ChatRoomDto.ResponseDto> roomInfo(@PathVariable String roomId) {
        return chatRoomService.findRoomById(roomId);
    }
    //endregion



}