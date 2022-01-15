package com.ppjt10.skifriend.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Photo {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private FreePost freePost;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String filePath;

    public Photo(String originalFileName, String filePath, FreePost freePost) {
        this.originalFileName = originalFileName;
        this.filePath = filePath;
        this.freePost = freePost;
    }
}
