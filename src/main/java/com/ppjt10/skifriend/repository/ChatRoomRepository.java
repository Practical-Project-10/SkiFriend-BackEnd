package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    //List<ChatRoom> findAllBySenderId(Long senderId);
    //ChatRoom findByCarpoolIdAndSenderId(Long carpoolId, Long senderId);
    //ChatRoom findByWriterIdAndSenderIdAndCarpoolId(Long writerId, Long senderId, Long carpoolId);
}
