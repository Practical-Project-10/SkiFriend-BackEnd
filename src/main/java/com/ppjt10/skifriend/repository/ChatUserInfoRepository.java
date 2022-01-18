package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.ChatUserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatUserInfoRepository extends JpaRepository<ChatUserInfo, Long> {
    ChatUserInfo findByUserIdAndOtherIdAndChatRoomCarpoolId(Long userId, Long otherId, Long carpoolId);
    void deleteByUserId(Long userId);
    List<ChatUserInfo> findAllByUserId(Long userId);
    List<ChatUserInfo> findAllByChatRoomId(Long chatRoomId);
    Optional<ChatUserInfo> findByUserIdAndChatRoomId(Long userId, Long roomId);
}
