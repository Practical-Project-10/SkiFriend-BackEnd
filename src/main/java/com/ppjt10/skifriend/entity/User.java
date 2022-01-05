package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.dto.signupdto.SignupRequestDto;
import com.ppjt10.skifriend.dto.userdto.UserProfileRequestDto;
import com.ppjt10.skifriend.dto.userdto.UserProfileUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phoneNum;

    @Column
    private String profileImg;

    @Column
    private String vacImg;

    @Column
    private String gender;

    @Column
    private String ageRange;

    @Column
    private String career;

    @Column
    private String selfIntro;

    // 태스트용 생성자
    public User(SignupRequestDto requestDto, String enPassword) {
        this.username = requestDto.getUsername();
        this.nickname = requestDto.getNickname();
        this.phoneNum = requestDto.getPhoneNum();
        this.password = enPassword;
        this.profileImg = "https://skifriendbucket.s3.ap-northeast-2.amazonaws.com/static/6950b535-5658-4604-8039-dd9d4e3a1119profile+picture.png";
    }

    public void setProfileImg(String imgPath) {
        this.profileImg = imgPath;
    }

    public void setVacImg(String imgPath) {
        this.vacImg = imgPath;
    }

    public void updatePassword(String enPassword) {
        this.password = enPassword;
    }

    public void update(UserProfileUpdateDto requestDto) {
        this.nickname = requestDto.getNickname();
        this.career = requestDto.getCareer();
        this.selfIntro = requestDto.getSelfIntro();
    }

    public void createUserProfile(UserProfileRequestDto requestDto) {
        this.gender = requestDto.getGender();
        this.ageRange = requestDto.getAgeRange();
        this.career = requestDto.getCareer();
        this.selfIntro = requestDto.getSelfIntro();
    }
}
