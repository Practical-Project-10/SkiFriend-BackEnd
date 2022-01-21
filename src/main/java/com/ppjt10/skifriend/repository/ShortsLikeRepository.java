package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Shorts;
import com.ppjt10.skifriend.entity.ShortsLike;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShortsLikeRepository extends JpaRepository<ShortsLike, Long> {
    Optional<ShortsLike> findByUserIdAndShortsId(Long userId, Long shortsId);
    void deleteAllByShortsId(Long shortsId);
    List<ShortsLike> findAllByShorts(Shorts shorts);
}
