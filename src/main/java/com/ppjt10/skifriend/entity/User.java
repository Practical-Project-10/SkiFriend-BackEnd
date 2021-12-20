package com.ppjt10.skifriend.entity;

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
    private String name;

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
}
