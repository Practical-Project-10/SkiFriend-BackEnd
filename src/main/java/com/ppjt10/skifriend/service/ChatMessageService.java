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
import com.ppjt10.skifriend.security.UserDetailsImpl;
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
        } else {
            return "";
        }
    }


    //region 해당 채팅방 모든 채팅 내용 불러오기
    public List<ChatMessageDto.ResponseDto> takeAllChatMessages(
            String roomId,
            UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        ChatRoom foundChatRoom = chatRoomRepository.findByRoomId(roomId);
        if(foundChatRoom.getSenderId() != userId && foundChatRoom.getWriterId() != userId) {
            throw new IllegalArgumentException("현재 참여중인 채팅방이 아닙니다");
        }
        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomRoomIdOrderByCreateAt(roomId);
        return chatMessages.stream()
                .map(e -> toChatMessageResponseDto(e))
                .collect(Collectors.toList());
    }
    //endregion

    //region 채팅방 메시지 보내기
    public void sendChatMessage(ChatMessageDto.RequestDto requestDto) {

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(requestDto.getRoomId());

        User user = userRepository.findByUsername(requestDto.getSender()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다")
        );

        ChatMessage message = new ChatMessage(requestDto.getType(), chatRoom, user, requestDto.getMessage());

        chatMessageRepository.save(message);

        ChatMessageDto.ResponseDto messageDto = ChatMessageDto.ResponseDto.builder()
                .roomId(message.getChatRoom().getRoomId())
                .type(message.getType())
                .messageId(message.getId())
                .message(message.getMessage())
                .sender(message.getUser().getNickname())
                .createdAt(message.getCreateAt().toString())
                .build();

        System.out.println("전송");
        redisPublisher.publish(messageDto);
        System.out.println("성공");
    }
    //endregion

    //region 채팅방 입장 구독 퇴장 메시지
    public void connectMessage(ChatMessageDto.RequestDto requestDto) {

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(requestDto.getRoomId());

        User user = userRepository.findByUsername(requestDto.getSender()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다")
        );

        ChatMessage message = new ChatMessage(requestDto.getType(), chatRoom, user, requestDto.getMessage());

        if (ChatMessage.MessageType.ENTER.equals(message.getType()))
            message.setMessage(message.getUser().getNickname() + "님이 입장하셨습니다.");
        else if (ChatMessage.MessageType.QUIT.equals(message.getType())) {
            message.setMessage(message.getUser().getNickname() + "님이 퇴장하셨습니다.");
        }


        ChatMessageDto.ResponseDto messageDto = ChatMessageDto.ResponseDto.builder()
                .roomId(message.getChatRoom().getRoomId())
                .type(message.getType())
                .message(message.getMessage())
                .sender(message.getUser().getNickname())
                .build();

        System.out.println("전송");
        redisPublisher.publish(messageDto);
        System.out.println("성공");
    }
    //endregion

    private ChatMessageDto.ResponseDto toChatMessageResponseDto(ChatMessage chatMessage) {
        return ChatMessageDto.ResponseDto.builder()
                .type(chatMessage.getType())
                .messageId(chatMessage.getId())
                .sender(chatMessage.getUser().getNickname())
                .message(chatMessage.getMessage())
                .createdAt(chatMessage.getCreateAt().toString())
                .build();
    }
}
