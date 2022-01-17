package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.ShortsLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortsLikeRepository extends JpaRepository<ShortsLike, Long> {
    ShortsLike findByUserIdAndAndShortsId(Long userId, Long shortsId);
}
