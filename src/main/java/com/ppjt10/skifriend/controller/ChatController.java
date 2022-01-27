package com.ppjt10.skifriend.controller;

import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessageRequestDto;
import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessageResponseDto;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.security.jwt.JwtDecoder;
import com.ppjt10.skifriend.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatMessageService chatMessageService;
    private final JwtDecoder jwtDecoder;

    // 해당 방에서 했던 모든 메시지 조회
    @GetMapping("/chat/message/{r6oomId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessageResponseDto>> getAllMessages(@PathVariable Long roomId,
                                                                       @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.ok().body(chatMessageService.getAllMessages(roomId, user));
    }

    // 채팅방 구독 및 메시지 보내기
    @MessageMapping("/chat/message")
    public void chatMessage(@Payload ChatMessageRequestDto requestDto,
                            @Header("Authorization") String token
    ) {
        token = token.substring(7);
        requestDto.setSender(jwtDecoder.decodeUsername(token));

        chatMessageService.sendChatMessage(requestDto);
    }


//    @MessageMapping("/chat/message/img")
//    public void uploadMessageImg(
//            @RequestPart("image") MultipartFile image,
//            ChatMessageDto.RequestDto requestDto,
//            @Header("Authorization") String token
//    ) throws IOException {
//        token = token.substring(7);
//
//        requestDto.setSender(jwtDecoder.decodeUsername(token));
//
//        chatMessageService.uploadChatMessageImg(image, requestDto);
//
//    }
}
