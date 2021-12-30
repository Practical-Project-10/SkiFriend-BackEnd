package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByUserIdAndFreePostId(Long userId, Long boardId);
    List<Likes> findAllByModifiedAtAfterAndFreePost_SkiResortId(LocalDateTime time, Long skiResort);
    List<Likes> findAllByFreePostId(Long boardId);
    List<Likes> deleteAllByFreePostId(Long boardId);
}
