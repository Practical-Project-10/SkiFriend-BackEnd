package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByChatRoomIdOrderByCreateAt(Long roomId);
    void deleteAllByChatRoomId(Long roomId);
    List<ChatMessage> findAllByChatRoomId(Long roomId);
}
