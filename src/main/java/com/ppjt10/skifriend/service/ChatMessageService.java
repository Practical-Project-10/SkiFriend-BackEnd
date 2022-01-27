package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.config.redispubsub.RedisPublisher;
import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessagePhoneNumDto;
import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessageRequestDto;
import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessageResponseDto;
import com.ppjt10.skifriend.entity.ChatMessage;
import com.ppjt10.skifriend.entity.ChatRoom;
import com.ppjt10.skifriend.entity.ChatUserInfo;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.*;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    //    private final MessageService messageService;
    private final ChatUserInfoRepository chatUserInfoRepository;
    private final RedisPublisher redisPublisher;
    private final RedisRepository redisRepository;
    private final UserRepository userRepository;
//    private final S3Uploader s3Uploader;
//    private final String imageDirName = "chatMessage";

    public Long getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return Long.parseLong(destination.substring(lastIndex + 1));
        } else {
            return null;
        }
    }

    // 해당 채팅방 모든 채팅 내용 불러오기
    @Transactional
    public List<ChatMessageResponseDto> getAllMessages(Long roomId, User user) {
        Long userId = user.getId();
        ChatUserInfo chatUserInfo = chatUserInfoRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow(
                () -> new IllegalArgumentException("현재 참여중인 채팅방이 아닙니다")
        );

        Long verifiedRoomId = chatUserInfo.getChatRoom().getId();
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoomIdOrderByCreateAt(verifiedRoomId);
        List<ChatMessageResponseDto> chatMessageResponseDtoList = new ArrayList<>();
        for (int i = 1; i < chatMessageList.size(); i++) {
            ChatMessage chatMessage = chatMessageList.get(i);
            if (!chatMessage.getUserId().equals(userId)) {
                chatMessage.setIsRead(true);
            }
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
    @Transactional
    public void sendChatMessage(ChatMessageRequestDto requestDto) {

        ChatRoom chatRoom = chatRoomRepository.findById(requestDto.getRoomId()).orElseThrow(
                () -> new IllegalArgumentException("해당 채팅방이 존재하지 않습니다")
        );
        Long roomId = chatRoom.getId();
        if (!chatRoom.isActive()) {
            throw new IllegalArgumentException("상대방이 채팅방을 퇴장했습니다.");
        }

        User user = userRepository.findByUsername(requestDto.getSender()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 유저가 존재하지 않습니다")
        );
        Long userId = user.getId();

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

        ChatMessage message = new ChatMessage(requestDto.getType(), chatRoom, userId, requestDto.getMessage());
        ChatUserInfo chatUserInfo = chatUserInfoRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow(
                () -> new IllegalArgumentException("해당 채팅방 정보가 존재하지 않습니다.")
        );
        User other = userRepository.findById(chatUserInfo.getOtherId()).orElseThrow(
                () -> new IllegalArgumentException("상대방이 존재하지 않습니다.")
        );
        if (redisRepository.getUserChatRoomInOut(roomId, other.getUsername())) {
            message.setIsRead(true);
        }

        if (ChatMessage.MessageType.PHONE_NUM.equals(message.getType())) {

            String phoneNum = user.getPhoneNum();
            message.setMessage(phoneNum.substring(0, 3) + "-" + phoneNum.substring(3, 7) + "-" + phoneNum.substring(7));

            chatMessageRepository.save(message);
//            messageService.openPhoneNumAlert(opponent.getPhoneNum(), phoneNum); // 문자메시지로 상대방한테 번호 전송
            ChatMessagePhoneNumDto messageDto = generateChatMessagePhoneNumDto(message, user.getNickname());

            System.out.println("전화번호 전송");
            redisPublisher.publishPhoneNum(messageDto);
            System.out.println("전화번호 전송 성공");

        } else {
            chatMessageRepository.save(message);

            ChatMessageResponseDto messageDto = generateChatMessageResponseDto(message, user, other.getId());

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
        Optional<User> user = userRepository.findById(chatMessage.getUserId());

        String profileImg;
        String nickname;
        if (user.isPresent()) {
            nickname = user.get().getNickname();
            profileImg = user.get().getProfileImg();
        } else {
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
    private ChatMessageResponseDto generateChatMessageResponseDto(ChatMessage chatMessage, User user, Long oppenentId) {
        return ChatMessageResponseDto.builder()
                .roomId(chatMessage.getChatRoom().getId())
                .type(chatMessage.getType())
                .messageId(chatMessage.getId())
                .message(chatMessage.getMessage())
                .receiverId(oppenentId)
                .sender(user.getNickname())
                .senderImg(user.getProfileImg())
                .createdAt(TimeConversion.timeChatConversion(chatMessage.getCreateAt()))
                .build();
    }

    // 전화번호 보내기
    private ChatMessagePhoneNumDto generateChatMessagePhoneNumDto(ChatMessage chatMessage, String nickname) {
        return ChatMessagePhoneNumDto.builder()
                .roomId(chatMessage.getChatRoom().getId())
                .type(chatMessage.getType())
                .message(chatMessage.getMessage())
                .sender(nickname)
                .build();
    }
}
