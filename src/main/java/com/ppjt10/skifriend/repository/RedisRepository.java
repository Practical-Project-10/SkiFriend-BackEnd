package com.ppjt10.skifriend.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;


import javax.annotation.Resource;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class RedisRepository {

    public static final String MESSAGE_COUNT = "MESSAGE_COUNT";
    public static final String ENTER_INFO = "ENTER_INFO";
    public static final String READ_CNT = "READ_CNT";

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, Integer> valueOperations;

    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

//    // 채팅방 유저수 조회
//    public long getMessageCount(String roomId) {
//        return Long.valueOf(Optional.ofNullable(valueOperations.get(MESSAGE_COUNT + "_" + roomId)).orElse(0));
//    }

//    // 채팅방에 입장한 유저수 +1
//    public long plusUserCount(String roomId) {
//        return Optional.ofNullable(valueOperations.increment(MESSAGE_COUNT + "_" + roomId)).orElse(0L);
//    }

    // 채팅방에 입장한 유저수 -1
//    public long minusUserCount(String roomId) {
//        return Optional.ofNullable(valueOperations.decrement(MESSAGE_COUNT + "_" + roomId)).filter(count -> count > 0).orElse(0L);
//    }

    // 채팅방 읽지 않은 메시지의 개수 조회
    public int getNotVerifiedMessage(String roomId) {
        return Math.toIntExact(Long.valueOf(Optional.ofNullable(valueOperations.get(MESSAGE_COUNT + "_" + roomId)).orElse(0)));
    }

    // 채팅방에서 읽지 않은 메시지의 개수 저장
    public int setNotVerifiedMessage(String roomId, int chatMessageCount) {
        return Math.toIntExact(Optional.ofNullable(valueOperations.increment(MESSAGE_COUNT + "_" + roomId, chatMessageCount)).orElse(0L));
    }


    public int getReadedMessage(String roomId, String name) {
        return Math.toIntExact(Long.valueOf(Optional.ofNullable(valueOperations.get(READ_CNT + "_" + roomId + "_" + name)).orElse(0)));
    }

    // 채팅방에서 읽지 않은 메시지의 개수 저장
    public int setReadedMessage(String roomId, String name, int chatMessageCount) {
        return Math.toIntExact(Optional.ofNullable(valueOperations.increment(READ_CNT + "_" + roomId + "_" + name, chatMessageCount)).orElse(0L));
    }
//
}
