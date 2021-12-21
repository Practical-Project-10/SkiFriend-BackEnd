package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findByFreePostIdAndUserId(Long postId, Long userId);
}
