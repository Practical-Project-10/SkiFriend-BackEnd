package com.ppjt10.skifriend.config;

import com.ppjt10.skifriend.entity.ChatUserInfo;
import com.ppjt10.skifriend.entity.User;
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
public class StompHandler implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;
    private final RedisRepository redisRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserInfoRepository chatUserInfoRepository;

    // websocket 을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            jwtDecoder.decodeUsername(accessor.getFirstNativeHeader("Authorization").substring(7));
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            System.out.println("SUBSCRIBE!!!!");

            Long roomId = chatMessageService.getRoomId(
                    Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId")
            );
            if (roomId != null) {
                String sessionId = (String) message.getHeaders().get("simpSessionId");
                System.out.println("클라이언트 헤더" + message.getHeaders());
                System.out.println("클라이언트 세션 아이디" + sessionId);
                redisRepository.setUserEnterInfo(sessionId, roomId);

                String name = jwtDecoder.decodeUsername(accessor.getFirstNativeHeader("Authorization").substring(7));
                System.out.println("클라이언트 유저 이름: " + name);
                redisRepository.setUserNameInfo(sessionId, name);
            }
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            System.out.println("DISCONNECT 클라이언트 sessionId: " + sessionId);
            Long roomId = redisRepository.getUserEnterRoomId(sessionId);
            String name = redisRepository.getUserNameId(sessionId);

            if (name != null && roomId != null) {
//                Optional<ChatRoom> chatRoom = chatRoomRepository.findById(roomId);
                saveReadMsgCnt(name, roomId);
            }
            redisRepository.removeUserEnterInfo(sessionId);
        }
        return message;
    }

    @Transactional
    public void saveReadMsgCnt(String name, Long roomId){
        Optional<User> user = userRepository.findByUsername(name);
        if (user.isPresent()) {
            int chatMessageCount = chatMessageRepository.findAllByChatRoomId(roomId).size();

            System.out.println("DISCONNECT 클라이언트 name: " + name);
            System.out.println("DISCONNECT 클라이언트 roomId: " + roomId);
            System.out.println("마지막으로 읽은 메세지 수 : " + chatMessageCount);

            ChatUserInfo chatUserInfo = chatUserInfoRepository.findByUserIdAndChatRoomId(user.get().getId(), roomId).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 채팅 정보가 없습니다.")
            );
            chatUserInfo.setReadMsgCnt(chatMessageCount);
        }
    }
}