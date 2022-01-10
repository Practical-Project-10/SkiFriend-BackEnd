package com.ppjt10.skifriend.config;

import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessageRequestDto;
import com.ppjt10.skifriend.entity.ChatMessage;
import com.ppjt10.skifriend.repository.ChatMessageRepository;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;
    private final ChatMessageService chatMessageService;
    private final RedisRepository redisRepository;
    private final ChatMessageRepository chatMessageRepository;

    // websocket 을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            jwtDecoder.decodeUsername(accessor.getFirstNativeHeader("Authorization").substring(7));
        }
        else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            System.out.println("SUBSCRIBE!!!!");

            String roomId = chatMessageService.getRoomId(
                    Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId")
            );
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            System.out.println("클라이언트 헤더" + message.getHeaders());
            System.out.println("클라이언트 세션 아이디" + sessionId);
            redisRepository.setUserEnterInfo(sessionId, roomId);

            String name = jwtDecoder.decodeUsername(accessor.getFirstNativeHeader("Authorization").substring(7));
            System.out.println("클라이언트 유저 이름: " + name);
            redisRepository.setUserNameInfo(sessionId, name);


//            chatMessageService.connectMessage(
//                    ChatMessageRequestDto.builder()
//                            .type(ChatMessage.MessageType.ENTER)
//                            .roomId(roomId)
//                            .sender(name)
//                            .build());
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            String roomId = redisRepository.getUserEnterRoomId(sessionId);
            String name = redisRepository.getUserNameId(sessionId);

            if (name != null) {
                System.out.println("DISCONNECT 클라이언트 유저 이름: " + name);
                int chatMessageCount = chatMessageRepository.findAllByChatRoomRoomId(roomId).size();
                System.out.println("마지막으로 읽은 메세지 수 : " + chatMessageCount);
                redisRepository.setLastReadMsgCnt(roomId, name, chatMessageCount);

                // 마지막 접속 시간 체크
                String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                redisRepository.setLastMessageReadTime(roomId, name, currentTime);

//                chatMessageService.connectMessage(ChatMessageRequestDto.builder()
//                        .type(ChatMessage.MessageType.QUIT)
//                        .roomId(roomId)
//                        .sender(name)
//                        .build());
            }
            redisRepository.removeUserEnterInfo(sessionId);
        }
        return message;
    }
}