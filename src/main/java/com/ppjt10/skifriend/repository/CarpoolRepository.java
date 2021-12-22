package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.List;

public interface CarpoolRepository extends JpaRepository<Carpool, Long> {

    List<Carpool> findAllByCarpoolTypeContainingAndStartLocationContainingAndEndLocationContainingAndDateAndMemberNumIsLessThanEqual(
            String carpoolType,
            String startLocation,
            String endLocation,
            String date,
            int maxMemberNum
    );

    List<Carpool> findAllByUser(User user);

    Page<Carpool> findAllBySkiResort(String skiResort, Pageable pageable);

}
