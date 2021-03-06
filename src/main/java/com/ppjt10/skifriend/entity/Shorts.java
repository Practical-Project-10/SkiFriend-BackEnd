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

    @Column(nullable = false)
    private String videoPath;

    @Column(nullable = false)
    private String thumbNailPath;

    @Column
    private int shortsLikeCnt;

    @Column
    private int shortsCommentCnt;

    public Shorts(Long userId, String title, String videoPath, String thumbNailPath){
        this.userId = userId;
        this.title = title;
        this.videoPath = videoPath;
        this.thumbNailPath = thumbNailPath;
    }

    public void update(String title){
        this.title = title;
    }

    public void setShortsCommentCnt(int shortsCommentCnt) {
        this.shortsCommentCnt = shortsCommentCnt;
    }

    public void setShortsLikeCnt(int shortsLikeCnt) {
        this.shortsLikeCnt = shortsLikeCnt;
    }
}
