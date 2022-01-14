package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.time.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    private Long carpoolId;

    public ChatRoom (String title, Long carpoolId) {
        //this.carpool = carpool;
        this.title = title;
        this.roomId = UUID.randomUUID().toString();
        this.carpoolId = carpoolId;
    }
}
