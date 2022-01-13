package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessagePhoneNumDto;
import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessageRequestDto;
import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessageResponseDto;
import com.ppjt10.skifriend.entity.ChatMessage;
import com.ppjt10.skifriend.entity.ChatRoom;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.config.redispubsub.RedisPublisher;
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

//    private final MessageService messageService;
//    private final ChatUserInfoRepository chatUserInfoRepository;
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
        if (!foundChatRoom.getSenderId().equals(userId) && !foundChatRoom.getWriterId().equals(userId)) {
            throw new IllegalArgumentException("현재 참여중인 채팅방이 아닙니다");
        }

        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoomRoomIdOrderByCreateAt(roomId);
        List<ChatMessageResponseDto> chatMessageResponseDtoList = new ArrayList<>();
        for (int i = 1; i < chatMessageList.size(); i++) {
            chatMessageResponseDtoList.add(generateChatMessageListResponseDto(chatMessageList.get(i)));
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

//        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.findAllByChatRoomId(chatRoom.getId());
//        User opponent;
//        if(chatUserInfoList.get(0).getUser().getId() != user.getId()) {
//            opponent = userRepository.findById(chatUserInfoList.get(1).getUser().getId()).orElseThrow(
//                    () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다")
//            );
//        } else {
//            opponent = userRepository.findById(chatUserInfoList.get(0).getUser().getId()).orElseThrow(
//                    () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다")
//            );
//        }

        ChatMessage message = new ChatMessage(requestDto.getType(), chatRoom, user.getId(), requestDto.getMessage());

        if (ChatMessage.MessageType.PHONE_NUM.equals(message.getType())) {

            String phoneNum = user.getPhoneNum();
            message.setMessage(phoneNum.substring(0, 3) + "-" + phoneNum.substring(3, 7) + "-" + phoneNum.substring(7));

            chatMessageRepository.save(message);
//            messageService.openPhoneNumAlert(opponent.getPhoneNum(), phoneNum); // 문자메시지로 상대방한테 번호 전송
            ChatMessagePhoneNumDto messageDto = generateChatMessagePhoneNumDto(message);

            System.out.println("전화번호 전송");
            redisPublisher.publishPhoneNum(messageDto);
            System.out.println("전화번호 전송 성공");

        } else {
            chatMessageRepository.save(message);

            ChatMessageResponseDto messageDto = generateChatMessageResponseDto(message);

            System.out.println("전송");
            redisPublisher.publish(messageDto);
            System.out.println("성공");
        }
    }

    // 상대방 전화번호 알림 메시지
//    public void phoneNumMessage(ChatMessageRequestDto requestDto) {
//
//        ChatRoom chatRoom = chatRoomRepository.findByRoomId(requestDto.getRoomId());
//
//        User user = userRepository.findByUsername(requestDto.getSender()).orElseThrow(
//                () -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다")
//        );
//
//        ChatMessage message = new ChatMessage(requestDto.getType(), chatRoom, user, requestDto.getMessage());
//
//        if (ChatMessage.MessageType.PHONE_NUM.equals(message.getType())) {
//            message.setMessage(message.getUser().getNickname() + "님의 번호는 " + message.getMessage() + "입니다");
//        }
//
//        ChatMessageResponseDto messageDto = ChatMessageResponseDto.builder()
//                .roomId(message.getChatRoom().getRoomId())
//                .type(message.getType())
//                .message(message.getMessage())
//                .build();
//
//        System.out.println("전송");
//        redisPublisher.publish(messageDto);
//        System.out.println("성공");
//    }

    // 저장된 메시지 목록 조회
    private ChatMessageResponseDto generateChatMessageListResponseDto(ChatMessage chatMessage) {
        String profileImg;
        String nickname;
        try {
            User user = userRepository.findById(chatMessage.getUserId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
            );
            nickname = user.getNickname();
            profileImg = user.getProfileImg();
        } catch (Exception e) {
            nickname = "알 수 없음";
            profileImg = "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/defalt+user+frofile.png";
        }

        return ChatMessageResponseDto.builder()
                .type(chatMessage.getType())
                .messageId(chatMessage.getId())
                .message(chatMessage.getMessage())
                .sender(nickname)
                .senderImg(profileImg)
                .createdAt(TimeConversion.timeChatConversion(chatMessage.getCreateAt()))
                .build();
    }

    // 메시지 보내기
    private ChatMessageResponseDto generateChatMessageResponseDto(ChatMessage chatMessage) {
        String profileImg;
        String nickname;
        try {
            User user = userRepository.findById(chatMessage.getUserId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
            );
            nickname = user.getNickname();
            profileImg = user.getProfileImg();
        } catch (Exception e) {
            nickname = "알 수 없음";
            profileImg = "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/defalt+user+frofile.png";
        }

        return ChatMessageResponseDto.builder()
                .roomId(chatMessage.getChatRoom().getRoomId())
                .type(chatMessage.getType())
                .messageId(chatMessage.getId())
                .message(chatMessage.getMessage())
                .sender(nickname)
                .senderImg(profileImg)
                .createdAt(TimeConversion.timeChatConversion(chatMessage.getCreateAt()))
                .build();
    }

    // 전화번호 보내기
    private ChatMessagePhoneNumDto generateChatMessagePhoneNumDto(ChatMessage chatMessage) {

        String nickname;
        try {
            User user = userRepository.findById(chatMessage.getUserId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 유저가 존재하지 않습니다.")
            );
            nickname = user.getNickname();
        } catch (Exception e) {
            nickname = "알 수 없음";
        }
        return ChatMessagePhoneNumDto.builder()
                .roomId(chatMessage.getChatRoom().getRoomId())
                .type(chatMessage.getType())
                .message(chatMessage.getMessage())
                .sender(nickname)
                .build();

    }

}
