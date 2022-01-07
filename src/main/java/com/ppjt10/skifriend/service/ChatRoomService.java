package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.certification.MessageService;
import com.ppjt10.skifriend.dto.chatroomdto.ChatRoomListResponseDto;
import com.ppjt10.skifriend.dto.chatroomdto.ChatRoomResponseDto;
import com.ppjt10.skifriend.entity.*;
import com.ppjt10.skifriend.repository.*;
import com.ppjt10.skifriend.time.TimeConversion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    public List<ChatRoomListResponseDto> getAllRooms(User user) {
        Long userId = user.getId();

        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.findAllByUserId(userId);
        List<ChatRoom> chatRoomList = new ArrayList<>();
        for (ChatUserInfo chatUserInfo : chatUserInfoList) {
            chatRoomList.add(chatUserInfo.getChatRoom());
        }

        List<ChatRoomListResponseDto> chatRoomListResponseDtoList = new ArrayList<>();

        for (ChatRoom chatRoom : chatRoomList) {
            ChatMessage chatMessage = chatMessageRepository.findAllByChatRoomRoomIdOrderByCreateAtDesc(chatRoom.getRoomId()).get(0);
            User other;
            if (chatRoom.getSenderId().equals(userId)) {
                other = userRepository.findById(chatRoom.getWriterId()).orElseThrow(
                        () -> new IllegalArgumentException("")
                );
            } else {
                other = userRepository.findById(chatRoom.getSenderId()).orElseThrow(
                        () -> new IllegalArgumentException("")
                );
            }
            chatRoomListResponseDtoList.add(generateChatRoomListResponseDto(chatRoom, chatMessage, other, user));
        }

        chatRoomListResponseDtoList.sort(ChatRoomListResponseDto::compareTo);

        return chatRoomListResponseDtoList;
    }


    // 유저가 참여한 특정 채팅방 조회 메소드
    public ChatRoomResponseDto getRoom(String roomId, User user) {
        Long userId = user.getId();
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        // 채팅방에 있는 모든 유저 정보 가져오기
        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.findAllByChatRoomId(chatRoom.getId());
        User opponent;
        if(chatUserInfoList.get(0).getUser().getId().equals(userId)) {
            opponent = chatUserInfoList.get(1).getUser();
        } else {
            opponent = chatUserInfoList.get(0).getUser();
        }
        return generateChatRoomResponseDto(chatRoom, opponent.getNickname());
    }

    // 채팅방 생성 메소드
    @Transactional
    public ChatRoomResponseDto createChatRoom(Long carpoolId, User sender) {

        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                () -> new IllegalArgumentException("해당 카풀 게시물은 존재하지 않습니다")
        );

        Long writerId = carpool.getUser().getId();
        Long senderId = sender.getId();
        if (writerId.equals(senderId)) {
            throw new IllegalArgumentException("채팅은 다른 유저와만 가능합니다");
        }

        User writer = userRepository.findById(writerId).orElseThrow(
                () -> new IllegalArgumentException("해당하는 유저가 없습니다")
        );

        String writerNickname = writer.getNickname();
        String writerUsername = writer.getUsername();
        String writerPhone = writer.getPhoneNum();

        ChatRoom existedChatRoom = chatRoomRepository.findByWriterIdAndSenderIdAndCarpoolId(writerId, senderId, carpoolId);
        //채팅방이 존재한다면
        if (existedChatRoom != null) {
            return generateChatRoomResponseDto(existedChatRoom, writerNickname);
        } else {    //존재하지 않는다면 방을 만들어준다.
            // 방 생성 알림 메세지 글 작성자한테 전송하기
            String msg = carpool.getTitle() + "게시글에 대한 채팅이 왔습니다! 확인하세요 :)";
            messageService.createChatRoomAlert(writerPhone, msg);

            ChatRoom chatRoom = new ChatRoom(carpool.getTitle(), writerId, senderId, carpoolId);
            chatRoomRepository.save(chatRoom);

            //방 생성시 첫 메시지 강제전송
            ChatMessage initMsg = new ChatMessage(ChatMessage.MessageType.ENTER, chatRoom, sender, ":)");

            chatMessageRepository.save(initMsg);

            // 작성자가 안 읽은 메시지 수를 저장
            redisRepository.setLastReadMsgCnt(chatRoom.getRoomId(), writerUsername, 0);

            //sender 정보
            ChatUserInfo chatUserInfoSender = new ChatUserInfo(sender, chatRoom);
            chatUserInfoRepository.save(chatUserInfoSender);

            //카풀 작성자 정보
            ChatUserInfo chatUserInfoWriter = new ChatUserInfo(writer, chatRoom);
            chatUserInfoRepository.save(chatUserInfoWriter);

            return generateChatRoomResponseDto(chatRoom, writerNickname);
        }
    }
    //endregion

    // 채팅방 생성
    private ChatRoomResponseDto generateChatRoomResponseDto(ChatRoom chatRoom, String nickName) {
        return ChatRoomResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .roomName(nickName)
                .longRoomId(chatRoom.getId())
                .build();
    }

    // 채팅방 목록 조회
    private ChatRoomListResponseDto generateChatRoomListResponseDto(
            ChatRoom chatRoom,
            ChatMessage chatMessage,
            User other,
            User user
    ) {
        String roomId = chatRoom.getRoomId();
        int presentChatMsgCnt = chatMessageRepository.findAllByChatRoomRoomId(roomId).size();
        int pastMsgCnt = redisRepository.getLastReadMsgCnt(roomId, user.getUsername());
        int notVerifiedMsgCnt = presentChatMsgCnt - pastMsgCnt;

        return ChatRoomListResponseDto.builder()
                .roomId(roomId)
                .longRoomId(chatRoom.getId())
                .roomName(other.getNickname())
                .lastMsg(chatMessage.getMessage())
                .lastMsgTime(TimeConversion.timeChatConversion(chatMessage.getCreateAt()))
                .notVerifiedMsgCnt(notVerifiedMsgCnt)
                .userProfile(other.getProfileImg())
                .build();
    }

}
