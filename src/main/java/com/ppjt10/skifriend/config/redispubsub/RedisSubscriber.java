package com.ppjt10.skifriend.config.redispubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessageResponseDto;
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
            ChatMessageResponseDto responseDto = objectMapper.readValue(publishedMessage,ChatMessageResponseDto.class);

            messagingTemplate.convertAndSend("/sub/chat/room/" + responseDto.getRoomId(), responseDto);
            messagingTemplate.convertAndSend("/sub/alarm/" + responseDto.getReceiverId(), responseDto);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}