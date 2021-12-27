package com.ppjt10.skifriend.entity;


import com.ppjt10.skifriend.time.Timestamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
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

    private MessageType type; // 메시지 타입

    @Column
    private String roomId; // 방번호

    @Column
    private String sender; // 메시지 보낸사람

    @Column
    private String message; // 메시지

    @Column
    private long userCount;
    //채팅이 이뤄지는 도중에 필요한 Dto를 따로 만들어 줘야함
//    @Builder
//    public ChatMessage(ChatMessageDto.RequestDto requestDto, UserService userService) {
//        this.type = requestDto.getType();
//        this.roomId = requestDto.getRoomId();
//        this.sender =  userService.getUser(requestDto.getSender()));
//        this.message = chatMessageRequestDto.getMessage();
//    }

    @Builder
    public ChatMessage(MessageType type, String roomId, String sender, String message, long userCount) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.userCount = userCount;
    }
}
