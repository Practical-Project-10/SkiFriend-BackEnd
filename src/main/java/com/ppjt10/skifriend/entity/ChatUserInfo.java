package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.time.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ChatUserInfo extends Timestamped {
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

    @Column
    private int readMsgCnt;

    public ChatUserInfo(Long userId, Long otherId, ChatRoom chatRoom) {
        this.userId = userId;
        this.otherId = otherId;
        this.chatRoom = chatRoom;
        this.readMsgCnt = 1;
    }

    public void setReadMsgCnt(int readMsgCnt){
        this.readMsgCnt = readMsgCnt;
    }
}
