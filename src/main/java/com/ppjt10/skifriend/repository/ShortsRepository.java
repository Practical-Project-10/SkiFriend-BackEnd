package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Shorts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShortsRepository extends JpaRepository<Shorts, Long> {
    List<Shorts> findAllByUserId(Long userId);
}
