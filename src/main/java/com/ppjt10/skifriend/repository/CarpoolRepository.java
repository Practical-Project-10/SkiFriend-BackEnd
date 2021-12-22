package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarpoolRepository extends JpaRepository<Carpool, Long> {
    List<Carpool> findAllByUser(User user);
}
