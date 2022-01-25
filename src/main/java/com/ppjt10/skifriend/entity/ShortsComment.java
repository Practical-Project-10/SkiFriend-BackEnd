package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.time.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ShortsComment extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Shorts shorts;

    @Column(nullable = false)
    private String content;

    public ShortsComment(Long userId, Shorts shorts, String content) {
        this.userId = userId;
        this.shorts = shorts;
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
    }
}
