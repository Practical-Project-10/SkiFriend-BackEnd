package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.ChatMessageDto;
import com.ppjt10.skifriend.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@RequiredArgsConstructor
@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;

    //region 해당 방에서 했던 모든 메시지 가져오기
    @GetMapping("/chat/message/{roomId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessageDto.ResponseDto>> getAllMessages(@PathVariable String roomId) {
        return chatMessageService.takeAllChatMessages(roomId);
    }
    //endregion

    //region 채팅방 구독 및 메시지 보내기
    @MessageMapping("/chat/message")
//    @SendTo("/chat/room/roomId")
    public void chatMessage(
            ChatMessageDto.RequestDto requestDto,
            @Header("token") String token
            ) {
        chatMessageService.sendChatMessage(requestDto, token);
    }
    //endregion

}
