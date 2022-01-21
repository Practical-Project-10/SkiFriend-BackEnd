package com.ppjt10.skifriend.config;

import com.ppjt10.skifriend.repository.*;
import com.ppjt10.skifriend.security.jwt.JwtDecoder;
import com.ppjt10.skifriend.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
@Transactional
public class StompHandler implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;
    private final ChatMessageService chatMessageService;
    private final RedisRepository redisRepository;

    // websocket 을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            jwtDecoder.decodeUsername(accessor.getFirstNativeHeader("Authorization").substring(7));
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            Long roomId = chatMessageService.getRoomId(
                    Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId")
            );
            if (roomId != null) {
                String sessionId = (String) message.getHeaders().get("simpSessionId");
                String name = jwtDecoder.decodeUsername(accessor.getFirstNativeHeader("Authorization").substring(7));
                redisRepository.setSessionUserInfo(sessionId, roomId, name);
                redisRepository.setUserChatRoomInOut(roomId + "_" + name, true);
                System.out.println("SUBSCRIBE 클라이언트 헤더" + message.getHeaders());
                System.out.println("SUBSCRIBE 클라이언트 세션 아이디" + sessionId);
                System.out.println("SUBSCRIBE 클라이언트 유저 이름: " + name);
            }
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String findInOutKey = redisRepository.getSessionUserInfo(sessionId);
            System.out.println("DISCONNECT 클라이언트 sessionId: " + sessionId);
            System.out.println("DISCONNECT 클라이언트 inoutKey: " + findInOutKey);

            if (findInOutKey != null) {
                redisRepository.setUserChatRoomInOut(findInOutKey, false);
            }

            redisRepository.removeUserEnterInfo(sessionId);
        }

        return message;
    }
}