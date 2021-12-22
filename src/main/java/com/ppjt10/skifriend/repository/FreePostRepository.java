package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.FreePost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface FreePostRepository extends JpaRepository<FreePost, Long> {
    List<FreePost> findAllBySkiResortOrderByLikeCntDesc(String skiResort);

    Page<FreePost> findAllBySkiResort(String skiResort, Pageable pageable);
}
