package com.ppjt10.skifriend.redispubsub;

import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessagePhoneNumDto;
import com.ppjt10.skifriend.dto.chatmessagedto.ChatMessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void publish(ChatMessageResponseDto messageDto) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), messageDto);
    }

    public void publishPhoneNum(ChatMessagePhoneNumDto messageDto) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), messageDto);
    }
}
