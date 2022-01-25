package com.ppjt10.skifriend.scheduler;

import com.ppjt10.skifriend.certification.MessageService;
import com.ppjt10.skifriend.entity.*;
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
import java.util.Optional;


@RequiredArgsConstructor
@Component
public class TimeScheduler {
    private final CarpoolRepository carpoolRepository;
    private final MessageService messageService;
    private final UserRepository userRepository;
    private final ChatUserInfoRepository chatUserInfoRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 15분 마다 실행
    @Transactional
    @Scheduled(cron = "0 0/15 * * * *")
    public void carpoolStatusScheduler() {
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println(currentDateTime + " 모집 기간 만료 체크");

        String[] dateTime = currentDateTime.split(" ");
        List<Carpool> carpoolList = carpoolRepository.findAllByDate(dateTime[0]);
        for (Carpool carpool : carpoolList) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime carpoolTime = LocalDateTime.parse(carpool.getTime(), formatter);
            LocalDateTime currentTime = LocalDateTime.parse(dateTime[1], formatter);
            Long timeDiff = Duration.between(carpoolTime, currentTime).getSeconds();
            if (timeDiff > 0) {
                carpool.setStatus(false);
            }
        }
    }

    // 15분 마다 실행
    @Transactional
    @Scheduled(cron = "0 0 0/1 * * *")
    public void chatAlertScheduler() {
        List<User> userList = new ArrayList<>();
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByModifiedAtAfterAndActive(LocalDateTime.now().minusHours(6), true);
        for(ChatRoom chatRoom : chatRoomList) {
            Long lastMsgId = chatRoom.getLastMessageId();
            ChatMessage notReadLastMsg = chatMessageRepository.findByIdAndReadMsg(lastMsgId, false);
            Long userId = notReadLastMsg.getUserId();
            ChatUserInfo chatUserInfo = chatUserInfoRepository.findByUserIdAndChatRoomId(userId, chatRoom.getId()).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 채팅방 정보가 없습니다")
            );
            User other = userRepository.findById(chatUserInfo.getOtherId()).orElseThrow(
                    () -> new IllegalArgumentException("해당하는 유저가 없습니다")
            );
            if(!userList.contains(other)){
                userList.add(other);
            }
        }
        for(User user : userList){
//            messageService.createChatRoomAlert(user.getPhoneNum(), "읽지 않은 메세지가 있습니다! 채팅방을 확인하세요 :)");
            System.out.println(user.getNickname() + "한테 알림왔대, 채팅방 확인좀해라");
        }
    }
}
