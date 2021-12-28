package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.dto.ChatRoomDto;
import com.ppjt10.skifriend.entity.*;
import com.ppjt10.skifriend.repository.*;
import com.ppjt10.skifriend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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

    //내가 참여한 모든 채팅방 목록 조회 메소드
    public List<ChatRoomDto.ChatRoomListResponseDto> findAllRoom(
            UserDetailsImpl userDetails
    ) {
        Long userId = userDetails.getUser().getId();

        List<Long> chatRoomIds = chatUserInfoRepository.findAllByUserId(userId).stream()
                .map(e->e.getChatRoom().getId())
                .collect(Collectors.toList());

        List<ChatRoom> chatRooms = chatRoomIds.stream()
                .map(e->chatRoomRepository.findById(e).orElseThrow(()->new IllegalArgumentException("해당하는 채팅방이 없습니다")))
                .collect(Collectors.toList());

        List<ChatRoomDto.ChatRoomListResponseDto> chatRoomListResponseDtos = chatRooms.stream()
                .map(e->toChatRoomListResponseDto(e, chatMessageRepository.findAllByChatRoomRoomIdOrderByCreateAtDesc(e.getRoomId()).get(0)))
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
        List<Long> userIdList = chatUserInfo.stream()
                .map(e->e.getUser().getId())
                .collect(Collectors.toList());
        if(!userIdList.contains(userId)) {
            throw new IllegalArgumentException("채팅방에 입장할 권한이 없습니다.");
        }
        return toChatRoomResponseDto(chatRoom);
    }
    //endregion

    //region 채팅방 생성 메소드
    @Transactional
    public ChatRoomDto.ResponseDto createChatRoom(
            Long carpoolId,
            UserDetailsImpl userDetails
    ) {
        if(userDetails == null){
            throw new IllegalArgumentException("로그인 한 유저만 채팅이 가능합니다.");
        }

        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                ()->new IllegalArgumentException("해당 카풀 게시물은 존재하지 않습니다")
        );

        Long writerId = carpool.getUser().getId();
        if (writerId == userDetails.getUser().getId()) {
            throw new IllegalArgumentException("채팅은 다른 유저와만 가능합니다");
        }

        Long senderId = userDetails.getUser().getId();
        ChatRoom existedChatRoom = chatRoomRepository.findByWriterIdAndSenderIdAndCarpoolId(writerId, senderId, carpoolId);

        if(existedChatRoom != null) {

            return toChatRoomResponseDto(existedChatRoom);

        }

        else {
            ChatRoom chatRoom = new ChatRoom(carpool.getTitle(), writerId, senderId, carpoolId);
            chatRoomRepository.save(chatRoom);

            ChatUserInfo chatUserInfoSender = new ChatUserInfo(userDetails.getUser(), chatRoom);
            chatUserInfoRepository.save(chatUserInfoSender);

            ChatUserInfo chatUserInfoWriter= new ChatUserInfo(carpool.getUser(), chatRoom);
            chatUserInfoRepository.save(chatUserInfoWriter);

            return toChatRoomResponseDto(chatRoom);
        }
    }
    //endregion


    private ChatRoomDto.ResponseDto toChatRoomResponseDto(ChatRoom chatRoom) {
        return ChatRoomDto.ResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .roomName(chatRoom.getTitle())
                .build();
    }

    private ChatRoomDto.ChatRoomListResponseDto toChatRoomListResponseDto(
            ChatRoom chatRoom,
            ChatMessage chatMessage) {
        return ChatRoomDto.ChatRoomListResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .roomName(chatRoom.getTitle())
                .lastMsg(chatMessage.getMessage())
                .lastMsgTime(chatMessage.getCreateAt().toString())
//                .userProfile()
                .build();
    }

}
