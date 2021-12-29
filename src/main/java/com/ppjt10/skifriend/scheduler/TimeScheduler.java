package com.ppjt10.skifriend.scheduler;

import com.ppjt10.skifriend.certification.MessageService;
import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.CarpoolRepository;
import com.ppjt10.skifriend.repository.RedisRepository;
import com.ppjt10.skifriend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;


@RequiredArgsConstructor
@Component
public class TimeScheduler {
    private final CarpoolRepository carpoolRepository;
    private final RedisRepository redisRepository;
    private final MessageService messageService;
    private final UserRepository userRepository;

    // 15분 마다 실행
    @Transactional
    @Scheduled(cron = "0 0/15 * * * *")
    public void carpoolStatusScheduler() {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println(currentTime + " 모집 기간 만료 체크");

        String[] dateTime = currentTime.split(" ");
        List<Carpool> carpoolList = carpoolRepository.findAllByDateAndTime(dateTime[0], dateTime[1]);
        for(Carpool carpool : carpoolList) {
            carpool.changeStatus();
        }
    }

    // 15분 마다 실행
    @Transactional
    @Scheduled(cron = "0 0 0/1 * * *")
    public void chatAlertScheduler() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<String> fromRedisLastMessageNameTimeList = redisRepository.getLastMessageReadTime();
        for (String fromRedisLastMessageNameTime : fromRedisLastMessageNameTimeList){
            List<String> lastMessageNameTime = Arrays.asList(fromRedisLastMessageNameTime.split("/"));
            String lastMessageName = lastMessageNameTime.get(0);
            LocalDateTime lastMessageTime = LocalDateTime.parse(lastMessageNameTime.get(1), formatter);

            Long timeDiff = Duration.between(lastMessageTime, currentTime).getSeconds();
            if ((timeDiff / 3600) > 0) { // 시간
                User user = userRepository.findByUsername(lastMessageName).orElseThrow(
                        () -> new IllegalArgumentException("해당하는 유저가 없습니다")
                );
                messageService.createChatRoomAlert(user.getPhoneNum(), "알림이 왔습니다 채팅방을 확인하세요");
            }
        }
    }
}
