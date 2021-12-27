package com.ppjt10.skifriend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence .*;
import java.util.ArrayList;
import java.util.List;

    @Entity
    @Getter
    @NoArgsConstructor
    public class SkiResort {
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id
        private Long id;

        @Column
        private String name;

        @OneToMany(mappedBy = "skiResort", cascade = CascadeType.ALL)
        @JsonIgnoreProperties({"skiResort"})
        private List<Carpool> carpoolList = new ArrayList<>();

        @OneToMany(mappedBy = "skiResort", cascade = CascadeType.ALL)
        @JsonIgnoreProperties({"skiResort"})
        private List<FreePost> freePostList = new ArrayList<>();
    }

