package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.dto.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNum;

    @Column
    private String profileImg;

    @Column
    private String vacImg;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String ageRange;

    @Column(nullable = false)
    private String career;

    @Column(nullable = false)
    private String selfIntro;

    public User(UserDto.RequestDto requestDto, String enPassword){
        this.username = requestDto.getUsername();
        this.nickname = requestDto.getNickname();
        this.phoneNum = requestDto.getPhoneNum();
        this.password = enPassword;
        this.gender = requestDto.getGender();
        this.ageRange = requestDto.getAgeRange();
        this.career = requestDto.getCareer();
        this.selfIntro = requestDto.getSelfIntro();
    }

    public void setProfileImg(String imgPath){
        this.profileImg = imgPath;
    }

    public void setVacImg(String imgPath){
        this.vacImg = imgPath;
    }

    public void update(UserDto.UpdateRequestDto requestDto, String enPassword) {
        this.password = enPassword;
        this.nickname = requestDto.getNickname();
        this.career = requestDto.getCareer();
        this.selfIntro = requestDto.getSelfIntro();
    }
}
