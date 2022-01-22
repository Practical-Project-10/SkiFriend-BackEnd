package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findAllByModifiedAtAfterAndActive(LocalDateTime localDateTime, Boolean active);
}
