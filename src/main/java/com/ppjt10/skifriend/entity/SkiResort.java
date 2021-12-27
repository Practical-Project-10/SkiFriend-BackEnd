package com.ppjt10.skifriend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SkiResort {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private String resortName;

    @OneToMany(mappedBy = "skiResort", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"skiResort"})
    private List<Carpool> carpoolList = new ArrayList<>();

    @OneToMany(mappedBy = "skiResort", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"skiResort"})
    private List<FreePost> freePostList = new ArrayList<>();
}

