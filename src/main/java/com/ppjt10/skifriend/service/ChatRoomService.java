package com.ppjt10.skifriend.service;


import com.ppjt10.skifriend.dto.ChatRoomDto;
import com.ppjt10.skifriend.entity.ChatRoom;
import com.ppjt10.skifriend.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    //region 전체 채팅방 조회 메소드
    public ResponseEntity<List<ChatRoomDto.ResponseDto>> findAllRoom() {
        // 채팅방 생성순서 최근 순으로 반환으로 변경해야함
        List<ChatRoom> chatRooms = chatRoomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        List<ChatRoomDto.ResponseDto> chatRoomResponseDtos = chatRooms.stream()
                .map(e->toChatRoomResponseDto(e))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(chatRoomResponseDtos);
    }
    //endregion

    //region 특정 채팅방 조회 메소드
    public ResponseEntity<ChatRoomDto.ResponseDto> findRoomById(String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        return ResponseEntity.ok().body(toChatRoomResponseDto(chatRoom));
    }
    //endregion

    //region 채팅방 만들기 메소드
    public ResponseEntity<ChatRoomDto.ResponseDto> createChatRoom(String name) {
        ChatRoom chatRoom = new ChatRoom(name);
        chatRoomRepository.save(chatRoom);
        return ResponseEntity.ok().body(toChatRoomResponseDto(chatRoom));
    }
    //endregion


    private ChatRoomDto.ResponseDto toChatRoomResponseDto(ChatRoom chatRoom) {
        return ChatRoomDto.ResponseDto.builder()
                .name(chatRoom.getName())
                .roomId(chatRoom.getRoomId())
                .build();
    }

}
