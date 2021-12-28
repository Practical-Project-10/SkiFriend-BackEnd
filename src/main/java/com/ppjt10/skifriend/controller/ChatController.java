package com.ppjt10.skifriend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ppjt10.skifriend.dto.ChatMessageDto;
import com.ppjt10.skifriend.repository.RedisRepository;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.security.jwt.HeaderTokenExtractor;
import com.ppjt10.skifriend.security.jwt.JwtDecoder;
import com.ppjt10.skifriend.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.List;


@RequiredArgsConstructor
@Controller
public class ChatController {
    private final RedisRepository redisRepository;
    private final ChatMessageService chatMessageService;
    private final JwtDecoder jwtDecoder;

    //region 해당 방에서 했던 모든 메시지 조회
    @GetMapping("/chat/message/{roomId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessageDto.ResponseDto>> getAllMessages(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {

        List<ChatMessageDto.ResponseDto> responseDtos = chatMessageService.takeAllChatMessages(
                roomId,
                userDetails
        );

        return ResponseEntity.ok().body(responseDtos);
    }
    //endregion

    //region 채팅방 구독 및 메시지 보내기
    @MessageMapping("/chat/message")
    public void chatMessage(
            @Payload ChatMessageDto.RequestDto requestDto,
            @Header("Authorization") String token
    ) throws ParseException, JsonProcessingException {
//        requestDto.setUserCount(redisRepository.getUserCount(requestDto.getRoomId()));

        token = token.substring(7);

        requestDto.setSender(jwtDecoder.decodeUsername(token));

        chatMessageService.sendChatMessage(requestDto);
    }
    //endregion

}
