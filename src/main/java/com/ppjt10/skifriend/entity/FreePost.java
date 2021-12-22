package com.ppjt10.skifriend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ppjt10.skifriend.dto.FreePostDto;
import com.ppjt10.skifriend.time.TimeConversion;
import com.ppjt10.skifriend.time.Timestamped;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FreePost extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn
    private User user;

    @Column(nullable = false)
    private String skiResort;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String Image;

    @OneToMany(mappedBy = "freePost", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"freePost"})
    List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "freePost", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"freePost"})
    List<Likes> likeList = new ArrayList<>();

    @Column
    private int likeCnt;
    
    @Column
    private int commentCnt;

    public void update(FreePostDto.RequestDto requestDto, String image) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.Image = image;
    }

    public FreePostDto.ResortTabDto toResortTabDto() {
        return FreePostDto.ResortTabDto.builder()
                .postId(id)
                .resortName(skiResort)
                .nickname(user.getNickname())
                .title(title)
                .content(content)
                .image(Image)
                .createdAt(TimeConversion.timeConversion(getCreateAt()))
                .likeCnt(likeCnt)
                .commentCnt(commentList.size())
                .build();
    }
}
