package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Likes;
import com.ppjt10.skifriend.entity.SkiResort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkiResortRepository extends JpaRepository<SkiResort, Long> {

    Optional<SkiResort> findByResortName(String skiResort);
}
