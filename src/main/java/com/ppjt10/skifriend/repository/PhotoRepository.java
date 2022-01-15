package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.FreePost;
import com.ppjt10.skifriend.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findAllByFreePost(FreePost freePost);
}
