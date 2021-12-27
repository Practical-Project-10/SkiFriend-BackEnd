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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Carpool carpool;

    @Column
    private Long senderId;

    public ChatRoom (Carpool carpool, Long senderId) {
        this.carpool = carpool;
        this.roomId = UUID.randomUUID().toString();
        this.senderId = senderId;
    }
}
