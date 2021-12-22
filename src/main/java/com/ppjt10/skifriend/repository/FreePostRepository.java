package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.entity.FreePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FreePostRepository extends JpaRepository<FreePost, Long> {
    Optional<FreePost> findByIdAndSkiResort(Long freePostId, String skiResort);
    List<FreePost> findAllBySkiResortOrderByLikeCntDesc(String skiResort);

    Page<FreePost> findAllBySkiResort(String skiResort, Pageable pageable);
//    Page<Carpool> findAllBySkiResort(String skiResort, Pageable pageable);
}
