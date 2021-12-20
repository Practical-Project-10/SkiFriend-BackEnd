package com.ppjt10.skifriend.entity;

import com.ppjt10.skifriend.time.Timestamped;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Carpool extends Timestamped {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String carpoolType;

    @Column(nullable = false)
    private String skiResult;

    @Column(nullable = false)
    private String startLocation;

    @Column(nullable = false)
    private String endLocation;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int memberNum;

    @Column(nullable = false)
    private String notice;
}
