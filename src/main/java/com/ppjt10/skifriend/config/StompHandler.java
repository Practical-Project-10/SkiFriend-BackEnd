package com.ppjt10.skifriend.config;

import com.ppjt10.skifriend.dto.ChatMessageDto;
import com.ppjt10.skifriend.entity.ChatMessage;
import com.ppjt10.skifriend.repository.RedisRepository;
import com.ppjt10.skifriend.security.jwt.JwtDecoder;
import com.ppjt10.skifriend.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

//token들 Authorization으로 바꿔줘야함
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;
    private final ChatMessageService chatMessageService;
    private final RedisRepository redisRepository;

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            jwtDecoder.decodeUsername(accessor.getFirstNativeHeader("Authorization").substring(7));
        }
//        else if(StompCommand.SUBSCRIBE == accessor.getCommand()) {
//            String roomId = chatMessageService.getRoomId(
//                    Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId")
//            );
//            String sessionId = (String) message.getHeaders().get("simpSessionId");
//            System.out.println("클라이언트 헤더" +message.getHeaders());
//            System.out.println("클라이언트 세션 아이디"+ sessionId);
//            redisRepository.setUserEnterInfo(sessionId, roomId);
//            redisRepository.plusUserCount(roomId);
//
//            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
//            System.out.println("클라이언트 유저 이름: " + name);
//            chatMessageService.sendChatMessage(
//                    ChatMessageDto.RequestDto .builder()
//                            .type(ChatMessage.MessageType.ENTER)
//                            .roomId(roomId)
//                            .sender(name)
//                            .build());
//        }
//        else if(StompCommand.DISCONNECT == accessor.getCommand()) {
//            String sessionId = (String) message.getHeaders().get("simpSessionId");
//            String roomId = redisRepository.getUserEnterRoomId(sessionId);
//            System.out.println("Disconnect시 룸아이디" +roomId);
//            redisRepository.minusUserCount(roomId);
//            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
//            System.out.println("클라이언트 유저 이름: " + name);
//            chatMessageService.sendChatMessage(ChatMessageDto.RequestDto.builder()
//                    .type(ChatMessage.MessageType.QUIT)
//                    .roomId(roomId)
//                    .sender(name)
//                    .build());
//            redisRepository.removeUserEnterInfo(sessionId);
//        }

        return message;
    }
}