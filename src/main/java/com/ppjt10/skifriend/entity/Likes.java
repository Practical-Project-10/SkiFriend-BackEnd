package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.dto.LikesDto;
import com.ppjt10.skifriend.time.Timestamped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Getter
@NoArgsConstructor
public class Likes extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private FreePost freePost;



    public Likes(User user, FreePost freePost) {
        this.user = user;
        this.freePost = freePost;
    }
}
