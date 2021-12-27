package com.ppjt10.skifriend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ppjt10.skifriend.time.Timestamped;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class ChatRoom extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Carpool carpool;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"chatRoom"})
    private List<ChatMessage> chatMessageList = new ArrayList<>();

    @Column
    private Long senderId;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"chatRoom"})
    private List<ChatUserInfo> chatUserInfoList = new ArrayList<>();

    @Builder
    public ChatRoom (Carpool carpool, List<ChatMessage> chatMessageList, Long senderId, List<ChatUserInfo> chatUserInfoList) {
        this.carpool = carpool;
        this.roomId = UUID.randomUUID().toString();
        this.chatMessageList = chatMessageList;
        this.senderId = senderId;
        this.chatUserInfoList = chatUserInfoList;
    }
}
