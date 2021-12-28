package com.ppjt10.skifriend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String roomId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(nullable = false)
//    private Carpool carpool;

    @Column
    private String notice;

    @Column
    private Long writerId;

    @Column
    private Long senderId;

    @Column
    private Long carpoolId;

    public ChatRoom (String notice, Long writerId, Long senderId, Long carpoolId) {
        //this.carpool = carpool;
        this.notice = notice;
        this.roomId = UUID.randomUUID().toString();
        this.writerId = writerId;
        this.senderId = senderId;
        this.carpoolId = carpoolId;
    }
}
