package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.dto.carpooldto.CarpoolRequestDto;
import com.ppjt10.skifriend.time.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Carpool extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private SkiResort skiResort;

    @Column(nullable = false)
    private String carpoolType;

    @Column(nullable = false)
    private String title;

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
    private String memberNum;

    @Column(nullable = false)
    private String notice;

    @Column(nullable = false)
    private boolean status;

    public Carpool(Long userId, CarpoolRequestDto requestDto, SkiResort skiResort) {
        this.userId = userId;
        this.carpoolType = requestDto.getCarpoolType();
        this.title = requestDto.getTitle();
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

    public void update(CarpoolRequestDto requestDto) {
        this.carpoolType = requestDto.getCarpoolType();
        this.startLocation = requestDto.getStartLocation();
        this.endLocation = requestDto.getEndLocation();
        this.title = requestDto.getTitle();
        this.date = requestDto.getDate();
        this.time = requestDto.getTime();
        this.price = requestDto.getPrice();
        this.memberNum = requestDto.getMemberNum();
        this.notice = requestDto.getNotice();
    }

    public void setStatus() {
        this.status = false;
    }
}
