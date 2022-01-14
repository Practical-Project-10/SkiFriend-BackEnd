package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.time.Timestamped;
import jdk.jfr.Timestamp;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ChatUserInfo {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long otherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ChatRoom chatRoom;

    public ChatUserInfo(Long userId, Long otherId, ChatRoom chatRoom) {
        this.userId = userId;
        this.otherId = otherId;
        this.chatRoom = chatRoom;
    }
}
