package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.certification.MessageService;
import com.ppjt10.skifriend.dto.ChatRoomDto;
import com.ppjt10.skifriend.entity.*;
import com.ppjt10.skifriend.repository.*;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final RedisRepository redisRepository;
    private final CarpoolRepository carpoolRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatUserInfoRepository chatUserInfoRepository;
    private final UserRepository userRepository;
    private final MessageService messageService;

    //내가 참여한 모든 채팅방 목록 조회 메소드
    public List<ChatRoomDto.ChatRoomListResponseDto> findAllRoom(
            UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        String username = userDetails.getUser().getUsername();

        List<Long> chatRoomIds = chatUserInfoRepository.findAllByUserId(userId).stream()
                .map(e -> e.getChatRoom().getId())
                .collect(Collectors.toList());

        List<ChatRoom> chatRooms = chatRoomIds.stream()
                .map(e -> chatRoomRepository.findById(e).orElseThrow(() -> new IllegalArgumentException("해당하는 채팅방이 없습니다")))
                .collect(Collectors.toList());

        List<ChatRoomDto.ChatRoomListResponseDto> chatRoomListResponseDtos = chatRooms.stream()
                .map(e -> toChatRoomListResponseDto(
                        e,
                        chatMessageRepository.findAllByChatRoomRoomIdOrderByCreateAtDesc(e.getRoomId()).get(0),
                        (e.getSenderId() == userId) ?
                                userRepository.findById(e.getWriterId()).orElseThrow(() -> new IllegalArgumentException("")).getNickname() :
                                userRepository.findById(e.getSenderId()).orElseThrow(() -> new IllegalArgumentException("")).getNickname(),
                        username,
                        (e.getSenderId() == userId) ?
                                userRepository.findById(e.getWriterId()).orElseThrow(() -> new IllegalArgumentException("")).getProfileImg() :
                                userRepository.findById(e.getSenderId()).orElseThrow(() -> new IllegalArgumentException("")).getProfileImg()
                ))
                .collect(Collectors.toList());
//        chatRooms.stream()
//                .forEach(chatRoom -> chatRoom.setUserCount(redisRepository.getUserCount(chatRoom.getRoomId())));
        return chatRoomListResponseDtos;
    }
    //

    //region 유저가 참여한 특정 채팅방 조회 메소드
    public ChatRoomDto.ResponseDto findRoomById(
            String roomId,
            UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        List<ChatUserInfo> chatUserInfo = chatUserInfoRepository.findAllByChatRoomId(chatRoom.getId());
        List<Long> inChatRoomUserIds = chatUserInfo.stream()
                .map(e -> e.getUser().getId())
                .collect(Collectors.toList());
        if (!inChatRoomUserIds.contains(userId)) {
            throw new IllegalArgumentException("채팅방에 입장할 권한이 없습니다.");
        }
        Long opponentId = inChatRoomUserIds.stream()
                .filter(e -> !e.equals(userId))
                .collect(Collectors.toList()).get(0);

        User user = userRepository.findById(opponentId).orElseThrow(
                () -> new IllegalArgumentException("해당 유저가 없습니다")
        );

        return toChatRoomResponseDto(chatRoom, user.getNickname());
    }
    //endregion

    //region 채팅방 생성 메소드
    @Transactional
    public ChatRoomDto.ResponseDto createChatRoom(
            Long carpoolId,
            UserDetailsImpl userDetails
    ) {
        if (userDetails == null) {
            throw new IllegalArgumentException("로그인 한 유저만 채팅이 가능합니다.");
        }

        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 카풀 게시물은 존재하지 않습니다")
        );

        Long writerId = carpool.getUser().getId();
        if (writerId == userDetails.getUser().getId()) {
            throw new IllegalArgumentException("채팅은 다른 유저와만 가능합니다");
        }

        Long senderId = userDetails.getUser().getId();
        ChatRoom existedChatRoom = chatRoomRepository.findByWriterIdAndSenderIdAndCarpoolId(writerId, senderId, carpoolId);
        User writer = userRepository.findById(writerId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 유저가 없습니다")
        );
        String writerNickname = writer.getNickname();
        String writerUsername = writer.getUsername();
        String writerPhone = writer.getPhoneNum();


        if (existedChatRoom != null) {
            return toChatRoomResponseDto(existedChatRoom, writerNickname);
        } else {
            // 방 생성 알림 메세지 글 작성자한테 전송하기
            String msg = carpool.getTitle() + "게시글에 대한 채팅이 왔습니다! 확인하세요 :)";
            messageService.createChatRoomAlert(writerPhone, msg);

            ChatRoom chatRoom = new ChatRoom(carpool.getTitle(), writerId, senderId, carpoolId);
            chatRoomRepository.save(chatRoom);

            redisRepository.setNotVerifiedMessage(chatRoom.getRoomId(), writerUsername, 0);

            ChatUserInfo chatUserInfoSender = new ChatUserInfo(userDetails.getUser(), chatRoom);
            chatUserInfoRepository.save(chatUserInfoSender);

            ChatUserInfo chatUserInfoWriter = new ChatUserInfo(carpool.getUser(), chatRoom);
            chatUserInfoRepository.save(chatUserInfoWriter);

            return toChatRoomResponseDto(chatRoom, writerNickname);
        }
    }
    //endregion


    private ChatRoomDto.ResponseDto toChatRoomResponseDto(ChatRoom chatRoom, String nickName) {
        return ChatRoomDto.ResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .roomName(nickName)
                .longRoomId(chatRoom.getId())
                .build();
    }

    private ChatRoomDto.ChatRoomListResponseDto toChatRoomListResponseDto(
            ChatRoom chatRoom,
            ChatMessage chatMessage,
            String nickname,
            String username,
            String userProfile
    ) {
        String roomId = chatRoom.getRoomId();
        int presentChatMsgCnt = chatMessageRepository.findAllByChatRoomRoomId(roomId).size();
        int pastMsgCnt = redisRepository.getNotVerifiedMessage(roomId, username);
        int notVerifiedMsgCnt = presentChatMsgCnt - pastMsgCnt;

        return ChatRoomDto.ChatRoomListResponseDto.builder()
                .roomId(roomId)
                .longRoomId(chatRoom.getId())
                .roomName(nickname)
                .lastMsg(chatMessage.getMessage())
                .lastMsgTime(TimeConversion.timeChatConversion(chatMessage.getCreateAt()))
                .notVerifiedMsgCnt(notVerifiedMsgCnt)
                .userProfile(userProfile)
                .build();
    }

}
