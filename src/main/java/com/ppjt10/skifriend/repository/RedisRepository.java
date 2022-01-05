package com.ppjt10.skifriend.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;


import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class RedisRepository {

    public static final String MESSAGE_COUNT = "MESSAGE_COUNT";
    public static final String ENTER_INFO = "ENTER_INFO";
    public static final String NAME_INFO = "NAME_INFO";
    public static final String LAST_MESSAGE_TIME = "LAST_MESSAGE_TIME";


    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, Integer> valueOperations;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> timeOperations;

    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    // 세션에 유저이름 저장
    public void setUserNameInfo(String sessionId, String name) {
        hashOpsEnterInfo.put(NAME_INFO, sessionId, name);
    }

    // 세션에서 유저이름 갖고오기
    public String getUserNameId(String sessionId) {
        return hashOpsEnterInfo.get(NAME_INFO, sessionId);
    }


    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }


    // 과거에 읽었던 메세지 개수 가져오기
    public int getLastReadMsgCnt(String roomId, String name) {
        return Math.toIntExact(Long.valueOf(Optional.ofNullable(valueOperations.get(MESSAGE_COUNT + "_" + roomId + "_" + name)).orElse(0)));
    }

    // 채팅방에서 DISCONNECT 시점에 읽은 메세지 개수 저장
    public void setLastReadMsgCnt(String roomId, String name, int chatMessageCount) {
        valueOperations.set(MESSAGE_COUNT + "_" + roomId + "_" + name, chatMessageCount);
    }

    // 마지막으로 읽은 시간 체크
    public void setLastMessageReadTime(String roomId, String name, String time){
        timeOperations.set(LAST_MESSAGE_TIME + "_" + roomId + "_" + name, name + "/" + time);
    }

    public List<String> getLastMessageReadTime() {
        Set<String> keys = timeOperations.getOperations().keys(LAST_MESSAGE_TIME+"*");
        return timeOperations.multiGet(keys);
    }
}
