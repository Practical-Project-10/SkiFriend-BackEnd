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

    //내가 참여한 채팅방 조회 메소드
    public ResponseEntity<List<ChatRoomDto.ResponseDto>> findAllRoom(
            UserDetailsImpl userDetails
    ) {
        Long senderId = userDetails.getUser().getId();
        // 채팅방 생성순서 최근 순으로 반환으로 변경해야함 -> 채팅 마지막으로 친 순서로 변경해야함
        List<ChatRoom> chatRooms = chatRoomRepository.findAllBySenderIdOrderByModifiedAt(senderId);
//        chatRooms.stream()
//                .forEach(chatRoom -> chatRoom.setUserCount(redisRepository.getUserCount(chatRoom.getRoomId())));
        List<ChatRoomDto.ResponseDto> chatRoomResponseDtos = chatRooms.stream()
                .map(e->toChatRoomResponseDto(e))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(chatRoomResponseDtos);
    }
    //

    //region 유저가 참여한 특정 채팅방 조회 메소드
    public ResponseEntity<ChatRoomDto.ResponseDto> findRoomById(
            String roomId,
            UserDetailsImpl userDetails
    ) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        List<Long> userIdList = chatRoom.getChatUserInfoList().stream()
                .map(e->e.getUser().getId())
                .collect(Collectors.toList());
        if(!userIdList.contains(userDetails.getUser().getId())) {
            throw new IllegalArgumentException("채팅방에 입장할 권한이 없습니다.");
        }
        return ResponseEntity.ok().body(toChatRoomResponseDto(chatRoom));
    }
    //endregion

    //region 채팅방 생성 메소드
    @Transactional
    public ResponseEntity<ChatRoomDto.ResponseDto> createChatRoom(
            Long carpoolId,
            UserDetailsImpl userDetails
    ) {
        Carpool carpool = carpoolRepository.findById(carpoolId).orElseThrow(
                ()->new IllegalArgumentException("해당 카풀 게시물은 존재하지 않습니다")
        );
//        if (carpool.getUser().getId() == userDetails.getUser().getId()) {
//            throw new IllegalArgumentException("채팅은 다른 유저와만 가능합니다");
//        }
        Long senderId = userDetails.getUser().getId();
        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.findAllByUserId(senderId);
        List<Long> userIds = chatUserInfoList.stream()
                .map(e->e.getUser().getId())
                .collect(Collectors.toList());
        if(userIds.contains(senderId)) {
            ChatRoom existedChatRoom = chatRoomRepository.findByCarpoolIdAndSenderId(carpoolId, senderId);
            return ResponseEntity.ok().body(toChatRoomResponseDto(existedChatRoom));
        }
        ChatRoom chatRoom = ChatRoom.builder()
                        .carpool(carpool)
                        .senderId(senderId)
                        .chatUserInfoList(chatUserInfoList)
                        .build();

        ChatUserInfo chatUserInfo = ChatUserInfo.builder()
                .user(userDetails.getUser())
                .chatRoom(chatRoom)
                .build();
        chatUserInfoRepository.save(chatUserInfo);
        return ResponseEntity.ok().body(toChatRoomResponseDto(chatRoom));
    }
    //endregion


    private ChatRoomDto.ResponseDto toChatRoomResponseDto(ChatRoom chatRoom) {
        return ChatRoomDto.ResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .roomName(chatRoom.getCarpool().getNotice())
                .build();
    }

}
