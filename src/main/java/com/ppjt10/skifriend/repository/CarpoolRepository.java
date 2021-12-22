package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Carpool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarpoolRepository extends JpaRepository<Carpool, Long> {
   Page<Carpool> findAllBySkiResort(String skiResort, Pageable pageable);
}
