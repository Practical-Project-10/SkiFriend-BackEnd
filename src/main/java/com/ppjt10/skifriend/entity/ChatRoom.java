package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.time.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class ChatRoom extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long carpoolId;

    @Column
    private boolean active;

    public ChatRoom(Long carpoolId) {
        this.carpoolId = carpoolId;
        this.active = true;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}