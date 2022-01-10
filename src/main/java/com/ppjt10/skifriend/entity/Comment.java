package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.dto.commentdto.CommentRequestDto;
import com.ppjt10.skifriend.time.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private FreePost freePost;

    @Column(nullable = false)
    private String content;

    public Comment(Long userId, FreePost freePost, String content){
        this.userId = userId;
        this.freePost = freePost;
        this.content = content;
    }

    public void update(CommentRequestDto requestDto) {
        this.content = requestDto.getContent();
    }
}
