package com.ppjt10.skifriend.entity;


import com.ppjt10.skifriend.time.Timestamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class ChatMessage  extends Timestamped {
    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, TALK , QUIT
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ChatRoom chatRoom; // 방번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user; // 메시지 보낸사람

    private MessageType type; // 메시지 타입

    @Column
    private String message; // 메시지

    @Column
    private String img; // 이미지 첨부시

    public ChatMessage(MessageType type, ChatRoom chatRoom, User user, String message) {
        this.type = type;
        this.chatRoom = chatRoom;
        this.user = user;
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
