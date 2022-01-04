package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.dto.freepostdto.FreePostRequestDto;
import com.ppjt10.skifriend.time.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class FreePost extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private SkiResort skiResort;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column
    private String Image;

    @Column
    private int likeCnt;

    @Column
    private int commentCnt;

    public void update(FreePostRequestDto requestDto, String image) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.Image = image;
    }

    public FreePost(User user, SkiResort skiResort, String title, String content, String image) {
        this.user = user;
        this.skiResort = skiResort;
        this.title = title;
        this.content = content;
        this.Image = image;
    }

    public void setLikeCnt(int likeCnt){
        this.likeCnt = likeCnt;
    }

    public void setCommentCnt(int commentCnt){
        this.commentCnt = commentCnt;
    }

}
