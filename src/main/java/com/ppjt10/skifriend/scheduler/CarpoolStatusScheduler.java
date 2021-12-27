package com.ppjt10.skifriend.scheduler;

import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.repository.CarpoolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RequiredArgsConstructor
@Component
public class CarpoolStatusScheduler {
    private final CarpoolRepository carpoolRepository;

    // 15분 마다 실행
    @Transactional
    @Scheduled(cron = "0 0/15 * * * *")
    public void updateStatus() {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println(currentTime + " 모집 기간 만료 체크");

        String[] dateTime = currentTime.split(" ");
        List<Carpool> carpoolList = carpoolRepository.findAllByDateAndTime(dateTime[0], dateTime[1]);
        for(Carpool carpool : carpoolList) {
            carpool.changeStatus();
        }
    }
}
