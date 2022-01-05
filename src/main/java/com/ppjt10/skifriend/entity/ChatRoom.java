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

    @Column
    private String title;

    @Column
    private Long writerId;

    @Column
    private Long senderId;

    @Column
    private Long carpoolId;

    public ChatRoom (String title, Long writerId, Long senderId, Long carpoolId) {
        //this.carpool = carpool;
        this.title = title;
        this.roomId = UUID.randomUUID().toString();
        this.writerId = writerId;
        this.senderId = senderId;
        this.carpoolId = carpoolId;
    }
}
