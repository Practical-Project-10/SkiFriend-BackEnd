package com.ppjt10.skifriend.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class ChatMessage  {
    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK , QUIT
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private MessageType type; // 메시지 타입

    @ManyToOne
    @JoinColumn
    private ChatRoom chatRoom; // 방번호

    @ManyToOne
    @JoinColumn
    private User user; // 메시지 보낸사람

    @Column
    private String message; // 메시지

    @Builder
    public ChatMessage(MessageType type, ChatRoom chatRoom, User user, String message) {
        this.type = type;
        this.chatRoom = chatRoom;
        this.user = user;
        this.message = message;
    }
}
