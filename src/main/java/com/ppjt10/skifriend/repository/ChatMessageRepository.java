package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoomIdOrderByCreateAt(Long roomId);
    void deleteAllByChatRoomId(Long roomId);

    ChatMessage findTopByChatRoomIdOrderByIdDesc(Long roomId);

    int countAllByChatRoomIdAndReadMsgAndUserId(Long roomId, boolean read, Long userId);
    ChatMessage findByIdAndReadMsg(Long id, boolean readMsg);

//    @Query(value = "SELECT MAX(m.id) FROM ChatMessage m WHERE m.chatRoom.id = :roomId")
//    Long findLastMsg(@Param("roomId") Long roomId);
}


//    SELECT A.content, A.createdAt, MAX(A.id)
//        FROM ChatMessage A Join ChatRoom B
//        ON A.chat_room_id = B.id

//    SELECT MAX(id) FROM Message WHERE A.id = B.chatroom_id;