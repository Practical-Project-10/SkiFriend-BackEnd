package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    User findByPhoneNum(String phoneNum);
    Boolean existsByUsername(String username);
}
