package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.ChatUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatUserInfoRepository extends JpaRepository<ChatUserInfo, Long> {
    List<ChatUserInfo> findAllByUserId(Long userId);
    List<ChatUserInfo> findAllByChatRoomId(Long chatRoomId);
}
