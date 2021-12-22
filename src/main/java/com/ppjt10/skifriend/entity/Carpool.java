package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.dto.CarpoolDto;
import com.ppjt10.skifriend.time.TimeConversion;
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

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String carpoolType;

    @Column(nullable = false)
    private String skiResort;

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

    public Carpool(User user, CarpoolDto.RequestDto requestDto, String skiResort) {
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


    public CarpoolDto.CategoryResponseDto toCatogoryResponseDto() {
        return CarpoolDto.CategoryResponseDto.builder()
                .postId(id)
                .userId(user.getId())
                .nickname(user.getNickname())
                .createdAt(TimeConversion.timeConversion(getCreateAt()))
                .carpoolType(carpoolType)
                .startLocation(startLocation)
                .endLocation(endLocation)
                .skiResort(skiResort)
                .date(date)
                .time(time)
                .price(price)
                .memberNum(memberNum)
                .notice(notice)
                .build();
        }
    public void changeStatus(){
        this.status = false;
    }
}
