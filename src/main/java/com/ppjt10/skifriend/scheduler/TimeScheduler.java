package com.ppjt10.skifriend.scheduler;

import com.ppjt10.skifriend.certification.MessageService;
import com.ppjt10.skifriend.entity.Carpool;
import com.ppjt10.skifriend.entity.User;
import com.ppjt10.skifriend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RequiredArgsConstructor
@Component
public class TimeScheduler {
    private final CarpoolRepository carpoolRepository;
    private final RedisRepository redisRepository;
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 15분 마다 실행
    @Transactional
    @Scheduled(cron = "0 0/15 * * * *")
    public void carpoolStatusScheduler() {
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println(currentTime + " 모집 기간 만료 체크");

        String[] dateTime = currentTime.split(" ");
        List<Carpool> carpoolList = carpoolRepository.findAllByDateAndTime(dateTime[0], dateTime[1]);
        for(Carpool carpool : carpoolList) {
            carpool.setStatus(false);
        }
    }

    // 15분 마다 실행
    @Transactional
    @Scheduled(cron = "0 0 0/1 * * *")
    public void chatAlertScheduler() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<String> userNameList = new ArrayList<>();
        List<String> fromRedisLastMsgNameTimeCntList = redisRepository.getLastMsgTimeCnt();
        for(String fromRedisLastMsgNameTimeCnt : fromRedisLastMsgNameTimeCntList) {
            List<String> lastMsgNameTimeCnt = Arrays.asList(fromRedisLastMsgNameTimeCnt.split("/"));
            String roomId = lastMsgNameTimeCnt.get(0);
            String lastMsgName = lastMsgNameTimeCnt.get(1);
            LocalDateTime lastMessageTime = LocalDateTime.parse(lastMsgNameTimeCnt.get(2), formatter);
            int lastMsgCnt = Integer.parseInt(lastMsgNameTimeCnt.get(3));
            if(userNameList.contains(lastMsgName)) {
                continue; // 아래 로직 실행하지 말고 다음 for 문을 돌아라
            }
            Long timeDiff = Duration.between(lastMessageTime, currentTime).getSeconds();
            int presentMsgCnt = chatMessageRepository.findAllByChatRoomRoomId(roomId).size();
            int restMsgCnt = presentMsgCnt - lastMsgCnt;
            if((timeDiff / 3600) > 0 && restMsgCnt >0) {
                userNameList.add(lastMsgName);
            }
        }
        for(String lastMessageName : userNameList) {
            User user = userRepository.findByUsername(lastMessageName).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 유저가 없습니다")
            );
//            messageService.createChatRoomAlert(user.getPhoneNum(), "알림이 왔습니다 채팅방을 확인하세요");
            System.out.println(user.getNickname() +"한테 알림왔대, 채팅방 확인좀해라");
        }
    }
}
