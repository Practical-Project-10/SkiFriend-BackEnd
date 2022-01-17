package com.ppjt10.skifriend.repository;

import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.entity.SkiResort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CarpoolRepository extends JpaRepository<Carpool, Long> {

    List<Carpool> findAllBySkiResortResortNameAndCarpoolTypeContainingAndStartLocationContainingAndEndLocationContainingAndDateContainingAndMemberNumIsContainingOrderByCreateAtDesc(
            String resortName,
            String carpoolType,
            String startLocation,
            String endLocation,
            String date,
            String memberNum
    );

    List<Carpool> findAllBySkiResortResortNameAndCarpoolTypeContainingAndStartLocationContainingAndEndLocationContainingAndDateContainingAndMemberNumIsContainingAndStatusOrderByCreateAtDesc(
            String resortName,
            String carpoolType,
            String startLocation,
            String endLocation,
            String date,
            String memberNum,
            boolean status
    );

    List<Carpool> findAllByUserId(Long userId);

    Page<Carpool> findAllBySkiResortOrderByCreateAtDesc(SkiResort skiResort, Pageable pageable);

    List<Carpool> findAllByDate(String date);
}
