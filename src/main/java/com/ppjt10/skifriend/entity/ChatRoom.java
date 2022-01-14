package com.ppjt10.skifriend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long carpoolId;

    public ChatRoom(Long carpoolId) {
        this.carpoolId = carpoolId;
    }
}