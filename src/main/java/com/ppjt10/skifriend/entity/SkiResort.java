package com.ppjt10.skifriend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class SkiResort {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String resortName;

    @Column(nullable = false, unique = true)
    private String resortImg;

    public SkiResort(String resortName, String resortImg){
        this.resortName = resortName;
        this.resortImg = resortImg;
    }
}
