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
        List<ChatRoom> chatRooms = chatRoomRepository.findAllBySenderId(senderId);
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
        Long userId = userDetails.getUser().getId();
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        List<ChatUserInfo> chatUserInfo = chatUserInfoRepository.findAllByChatRoomId(chatRoom.getId());
        List<Long> userIdList = chatUserInfo.stream()
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
        List<ChatUserInfo> chatUserInfoList = chatUserInfoRepository.findAllByUserId(senderId);
        List<Long> userIds = chatUserInfoList.stream()
                .map(e->e.getUser().getId())
                .collect(Collectors.toList());

        if(userIds.contains(senderId)) {
            ChatRoom existedChatRoom = chatRoomRepository.findByWriterIdAndSenderId(writerId, senderId);
            return ResponseEntity.ok().body(toChatRoomResponseDto(existedChatRoom));
        }

        ChatRoom chatRoom = new ChatRoom(carpool.getNotice(), writerId, senderId);
        chatRoomRepository.save(chatRoom);
        System.out.println("채팅 방 저장!!!!!!!!!");

        ChatUserInfo chatUserInfoSender = new ChatUserInfo(userDetails.getUser(), chatRoom);
        chatUserInfoRepository.save(chatUserInfoSender);

        ChatUserInfo chatUserInfoWriter= new ChatUserInfo(carpool.getUser(), chatRoom);
        chatUserInfoRepository.save(chatUserInfoWriter);

        return ResponseEntity.ok().body(toChatRoomResponseDto(chatRoom));
    }
    //endregion


    private ChatRoomDto.ResponseDto toChatRoomResponseDto(ChatRoom chatRoom) {
        return ChatRoomDto.ResponseDto.builder()
                .roomId(chatRoom.getRoomId())
                .roomName(chatRoom.getNotice())
                .build();
    }

}
