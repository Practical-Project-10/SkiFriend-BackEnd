package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.dto.LikesDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Likes {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private FreePost freePost;

    public LikesDto.ResponseDto toResponseDto() {
        return LikesDto.ResponseDto.builder()
                .userId(user.getId())
                .build();
    }
}
