package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.FreePost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreePostRepository extends JpaRepository<FreePost, Long> {
    FreePost findByIdAndSkiResort(Long freePostId, String skiResort);
    FreePost deleteByIdAndSkiResort(Long freePostId, String skiResort);
}
