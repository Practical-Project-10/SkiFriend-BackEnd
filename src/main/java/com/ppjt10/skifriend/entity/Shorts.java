package com.ppjt10.skifriend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Shorts {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column
    private String videoPath;

    @Column
    private int shortsLikeCnt;

    @Column
    private int shortsCommentCnt;

    public Shorts(Long userId, String title){
        this.userId = userId;
        this.title = title;
    }

    public void setShortsCommentCnt(int shortsCommentCnt) {
        this.shortsCommentCnt = shortsCommentCnt; }
}
