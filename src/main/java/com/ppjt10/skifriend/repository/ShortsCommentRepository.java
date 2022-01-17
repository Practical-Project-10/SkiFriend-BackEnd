package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Shorts;
import com.ppjt10.skifriend.entity.ShortsComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShortsCommentRepository extends JpaRepository<ShortsComment, Long> {
    List<ShortsComment> findAllByShorts(Shorts shorts);
}
