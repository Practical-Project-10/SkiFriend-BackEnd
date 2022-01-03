package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByFreePostId(Long boardId);
    List<Comment> deleteAllByFreePostId(Long boardId);
}
