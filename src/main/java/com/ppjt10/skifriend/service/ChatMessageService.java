package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.dto.ChatMessageDto;
import com.ppjt10.skifriend.entity.ChatMessage;
import com.ppjt10.skifriend.entity.ChatRoom;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.redispubsub.RedisPublisher;
import com.ppjt10.skifriend.repository.ChatMessageRepository;
import com.ppjt10.skifriend.repository.ChatRoomRepository;
import com.ppjt10.skifriend.repository.RedisRepository;
import com.ppjt10.skifriend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
//    private final SimpMessageSendingOperations messaging;
    private final RedisRepository redisRepository;
    private final RedisPublisher redisPublisher;
    private final UserRepository userRepository;

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        }
        else {
            return "";
        }
    }


    //region 해당 채팅방 모든 채팅 내용 불러오기
    public ResponseEntity<ChatMessageDto.InChatRoomResponseDto> takeAllChatMessages(String roomId) {
        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomRoomIdOrderByCreateAt(roomId);
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        List<ChatMessageDto.ResponseDto> chatMessageResponseDtos = chatMessages.stream()
                .map(e->toChatMessageResponseDto(e))
                .collect(Collectors.toList());
        ChatMessageDto.InChatRoomResponseDto inChatRoomResponseDto = ChatMessageDto.InChatRoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .roomName(chatRoom.getCarpool().getNotice())
                .roomContents(chatMessageResponseDtos)
                .build();
        return ResponseEntity.ok().body(inChatRoomResponseDto);
    }
    //endregion

    //region 채팅방 구독하기/ 메시지 보내기
    public void sendChatMessage(ChatMessageDto.RequestDto requestDto) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(requestDto.getRoomId());
        User user = userRepository.findByUsername(requestDto.getSender()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다")
        );
        ChatMessage message = ChatMessage.builder()
                .type(requestDto.getType())
                .chatRoom(chatRoom)
                .user(user)
                .message(requestDto.getMessage())
                .build();
//        MessageDto message = MessageDto.builder()
//                .type(requestDto.getType())
//                .nickname(user.getNickname())
//                .message(requestDto.getMessage())
//                .build();

//        if (ChatMessage.MessageType.ENTER.equals(message.getType()))
//            message.setMessage(message.getUser().getNickname() + "님이 입장하셨습니다.");
//        else if (ChatMessage.MessageType.QUIT.equals(message.getType())) {
//            message.setMessage(message.getUser().getNickname() + "님이 퇴장하셨습니다.");
//        }
//        chatMessageRepository.save(message);
        System.out.println("전송");
        redisPublisher.publish(message);
        System.out.println("성공");
    }
    //endregion

    private ChatMessageDto.ResponseDto toChatMessageResponseDto(ChatMessage chatMessage) {
        return ChatMessageDto.ResponseDto.builder()
                .type(chatMessage.getType())
                .roomId(chatMessage.getChatRoom().getRoomId())
                .sender(chatMessage.getUser().getNickname())
                .message(chatMessage.getMessage())
                .build();
    }
}
