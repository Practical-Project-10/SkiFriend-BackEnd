package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.dto.SignupDto;
import com.ppjt10.skifriend.dto.UserDto;
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
    public User(SignupDto.RequestDto requestDto, String enPassword){
        this.username = requestDto.getUsername();
        this.nickname = requestDto.getNickname();
        this.phoneNum = requestDto.getPhoneNum();
        this.password = enPassword;
    }

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

    public void setPassword(String enPassword) { this.password = enPassword; }

    public void update(UserDto.UpdateRequestDto requestDto) {
        this.nickname = requestDto.getNickname();
        this.career = requestDto.getCareer();
        this.selfIntro = requestDto.getSelfIntro();
    }

    public void wirteProfile(UserDto.ProfileRequestDto requestDto) {
        this.gender = requestDto.getGender();
        this.ageRange = requestDto.getAgeRange();
        this.career = requestDto.getCareer();
        this.selfIntro = requestDto.getSelfIntro();
    }
}
