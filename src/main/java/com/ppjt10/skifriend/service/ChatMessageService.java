package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.dto.ChatMessageDto;
import com.ppjt10.skifriend.entity.ChatMessage;
import com.ppjt10.skifriend.redispubsub.RedisPublisher;
import com.ppjt10.skifriend.repository.ChatMessageRepository;
import com.ppjt10.skifriend.repository.RedisRepository;
import com.ppjt10.skifriend.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    //    private final SimpMessageSendingOperations messaging;
    private final RedisRepository redisRepository;
    private final RedisPublisher redisPublisher;

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        } else {
            return "";
        }
    }


    //region 해당 채팅방 모든 채팅 내용 불러오기
    public ResponseEntity<List<ChatMessageDto.ResponseDto>> takeAllChatMessages(String roomId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findAllByRoomId(roomId);
        List<ChatMessageDto.ResponseDto> chatMessageResponseDtos = chatMessages.stream()
                .map(e -> toChatMessageResponseDto(e))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(chatMessageResponseDtos);
    }
    //endregion

    //region 채팅방 구독하기/ 메시지 보내기
    public void sendChatMessage(ChatMessageDto.RequestDto requestDto) {
//        String nickname = jwtDecoder.decodeUsername(token);

        ChatMessage message = new ChatMessage(requestDto.getType(),
                requestDto.getRoomId(),
                requestDto.getSender(),
                requestDto.getMessage(),
                requestDto.getUserCount());
//        ChatMessage message = new ChatMessage(requestDto.getType(), requestDto.getRoomId(),nickname , requestDto.getMessage());
        if (ChatMessage.MessageType.ENTER.equals(message.getType()))
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        else if (ChatMessage.MessageType.QUIT.equals(message.getType())) {
            message.setMessage(message.getSender() + "님이 퇴장하셨습니다.");
        }
        chatMessageRepository.save(message);
        System.out.println("전송");
//        messaging.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        redisPublisher.publish(message);
        System.out.println("성공");
    }
    //endregion

    private ChatMessageDto.ResponseDto toChatMessageResponseDto(ChatMessage chatMessage) {
        return ChatMessageDto.ResponseDto.builder()
                .type(chatMessage.getType())
                .roomId(chatMessage.getRoomId())
                .sender(chatMessage.getSender())
                .message(chatMessage.getMessage())
                .build();
    }
}
