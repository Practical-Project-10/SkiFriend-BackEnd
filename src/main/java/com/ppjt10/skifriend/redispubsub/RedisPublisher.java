package com.ppjt10.skifriend.redispubsub;

import com.ppjt10.skifriend.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void publish(ChatMessage message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
