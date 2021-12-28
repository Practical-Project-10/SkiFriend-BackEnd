package com.ppjt10.skifriend.redispubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppjt10.skifriend.dto.ChatMessageDto;
import com.ppjt10.skifriend.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final SimpMessageSendingOperations messagingTemplate;

    public void sendMessage(String publishedMessage) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
//            ChatMessage chatMessage = objectMapper.readValue(publishedMessage,ChatMessage.class);
            ChatMessageDto.ResponseDto responseDto = objectMapper.readValue(publishedMessage,ChatMessageDto.ResponseDto.class);
            messagingTemplate.convertAndSend("/sub/chat/room/" + responseDto.getRoomId(), responseDto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}