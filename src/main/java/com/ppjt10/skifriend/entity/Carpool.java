package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.dto.CarpoolDto;
import com.ppjt10.skifriend.time.TimeConversion;
import com.ppjt10.skifriend.time.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Carpool extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @OneToMany(mappedBy = "carpool", cascade = CascadeType.ALL)
    private List<ChatRoom> chatRoomList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(nullable = false)
    private SkiResort skiResort;

    @Column(nullable = false)
    private String carpoolType;

    @Column(nullable = false)
    private String startLocation;

    @Column(nullable = false)
    private String endLocation;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int memberNum;

    @Column(nullable = false)
    private String notice;

    @Column(nullable = false)
    private boolean status;

    public Carpool(User user, CarpoolDto.RequestDto requestDto, SkiResort skiResort) {
        this.user = user;
        this.carpoolType = requestDto.getCarpoolType();
        this.skiResort = skiResort;
        this.startLocation = requestDto.getStartLocation();
        this.endLocation = requestDto.getEndLocation();
        this.date = requestDto.getDate();
        this.time = requestDto.getTime();
        this.price = requestDto.getPrice();
        this.memberNum = requestDto.getMemberNum();
        this.notice = requestDto.getNotice();
        this.status = true;
    }

    public void update(CarpoolDto.RequestDto requestDto) {
        this.carpoolType = requestDto.getCarpoolType();
        this.startLocation = requestDto.getStartLocation();
        this.endLocation = requestDto.getEndLocation();
        this.date = requestDto.getDate();
        this.time = requestDto.getTime();
        this.price = requestDto.getPrice();
        this.memberNum = requestDto.getMemberNum();
        this.notice = requestDto.getNotice();
    }

    public void changeStatus() {
        this.status = false;
    }
}
