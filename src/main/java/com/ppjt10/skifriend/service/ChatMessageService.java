package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.config.S3Uploader;
import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessageRequestDto;
import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessageResponseDto;
import com.ppjt10.skifriend.entity.ChatMessage;
import com.ppjt10.skifriend.entity.ChatRoom;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.redispubsub.RedisPublisher;
import com.ppjt10.skifriend.repository.ChatMessageRepository;
import com.ppjt10.skifriend.repository.ChatRoomRepository;
import com.ppjt10.skifriend.repository.UserRepository;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
//    private final RedisRepository redisRepository;
    private final RedisPublisher redisPublisher;
    private final UserRepository userRepository;
//    private final S3Uploader s3Uploader;
//    private final String imageDirName = "chatMessage";

    // 채팅방 String Id 값 가져오기, Url 생성에 이용
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1);
        } else {
            return "";
        }
    }

    // 해당 채팅방 모든 채팅 내용 불러오기
    public List<ChatMessageResponseDto> getAllMessages(String roomId, User user) {
        Long userId = user.getId();
        ChatRoom foundChatRoom = chatRoomRepository.findByRoomId(roomId);
        if(!foundChatRoom.getSenderId().equals(userId) && !foundChatRoom.getWriterId().equals(userId)) {
            throw new IllegalArgumentException("현재 참여중인 채팅방이 아닙니다");
        }

        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoomRoomIdOrderByCreateAt(roomId);
        List<ChatMessageResponseDto> chatMessageResponseDtoList = new ArrayList<>();
        for(int i=1; i<chatMessageList.size(); i++) {
            chatMessageResponseDtoList.add(generateChatMessageResponseDto(chatMessageList.get(i)));
        }

        return chatMessageResponseDtoList;
    }

    //region 채팅방 사진 메시지 보내기
//    public void uploadChatMessageImg(MultipartFile img, ChatMessageDto.RequestDto requestDto) {
//
//        ChatRoom chatRoom = chatRoomRepository.findByRoomId(requestDto.getRoomId());
//
//        User user = userRepository.findByUsername(requestDto.getSender()).orElseThrow(
//                () -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다")
//        );
//
//        String imageUrl;
//        try {
//            imageUrl = s3Uploader.upload(img, imageDirName);
//        } catch (Exception err) {
//            imageUrl = "No Message Image";
//        }
//
//        ChatMessage message = new ChatMessage(requestDto.getType(), chatRoom, user, requestDto.getMessage());
//
//        message.setImg(imageUrl);
//
//        chatMessageRepository.save(message);
//
//        ChatMessageDto.ResponseDto messageDto = ChatMessageDto.ResponseDto.builder()
//                .roomId(message.getChatRoom().getRoomId())
//                .type(message.getType())
//                .messageId(message.getId())
//                .img(message.getImg())
//                .sender(message.getUser().getNickname())
//                .senderImg(message.getUser().getProfileImg())
//                .createdAt(TimeConversion.timeChatConversion(message.getCreateAt()))
//                .build();
//
//        System.out.println("전송");
//        redisPublisher.publish(messageDto);
//        System.out.println("성공");
//    }
    //endregion



    // 채팅방 메시지 보내기
    public void sendChatMessage(ChatMessageRequestDto requestDto) {

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(requestDto.getRoomId());

        User user = userRepository.findByUsername(requestDto.getSender()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다")
        );

        ChatMessage message = new ChatMessage(requestDto.getType(), chatRoom, user, requestDto.getMessage());

        chatMessageRepository.save(message);

        ChatMessageResponseDto messageDto = ChatMessageResponseDto.builder()
                .roomId(message.getChatRoom().getRoomId())
                .type(message.getType())
                .messageId(message.getId())
                .message(message.getMessage())
                .sender(message.getUser().getNickname())
                .senderImg(message.getUser().getProfileImg())
                .createdAt(TimeConversion.timeChatConversion(message.getCreateAt()))
                .build();

        System.out.println("전송");
        redisPublisher.publish(messageDto);
        System.out.println("성공");
    }

    // 채팅방 입장 구독 퇴장 메시지
//    public void connectMessage(ChatMessageRequestDto requestDto) {
//
//        ChatRoom chatRoom = chatRoomRepository.findByRoomId(requestDto.getRoomId());
//
//        User user = userRepository.findByUsername(requestDto.getSender()).orElseThrow(
//                () -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다")
//        );
//
//        ChatMessage message = new ChatMessage(requestDto.getType(), chatRoom, user, requestDto.getMessage());
//
//        if (ChatMessage.MessageType.ENTER.equals(message.getType()))
//            message.setMessage(message.getUser().getNickname() + "님이 입장하셨습니다.");
//        else if (ChatMessage.MessageType.QUIT.equals(message.getType())) {
//            message.setMessage(message.getUser().getNickname() + "님이 퇴장하셨습니다.");
//        }
//
//
//        ChatMessageResponseDto messageDto = ChatMessageResponseDto.builder()
//                .roomId(message.getChatRoom().getRoomId())
//                .type(message.getType())
//                .message(message.getMessage())
//                .sender(message.getUser().getNickname())
//                .build();
//
//        System.out.println("전송");
//        redisPublisher.publish(messageDto);
//        System.out.println("성공");
//    }


    private ChatMessageResponseDto generateChatMessageResponseDto(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                .type(chatMessage.getType())
                .messageId(chatMessage.getId())
                .sender(chatMessage.getUser().getNickname())
                .senderImg(chatMessage.getUser().getProfileImg())
                .message(chatMessage.getMessage())
                .createdAt(TimeConversion.timeChatConversion(chatMessage.getCreateAt()))
                .build();
    }
}
