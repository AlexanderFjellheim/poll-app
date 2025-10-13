package com.dat250.poll_app.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class VoteCountCacheService {
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    private final RedisTemplate<String, String> redisTemplate;

    public VoteCountCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String key(Long pollId) {
        return "poll:" + pollId + ":votes";
    }

    public LinkedHashMap<Integer, Long> get(Long pollId) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key(pollId));
        if (entries.isEmpty()) return null;
        LinkedHashMap<Integer, Long> votes = new LinkedHashMap<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            votes.put(Integer.parseInt((String) entry.getKey()), Long.parseLong((String) entry.getValue()));
        }
        return votes;
    }

    public void put(Long pollId, Map<Integer, Long> counts) {
        String key = key(pollId);
        for (Map.Entry<Integer, Long> entry : counts.entrySet()) {
            redisTemplate.opsForHash().put(key, entry.getKey().toString(), entry.getValue().toString());
        }
        redisTemplate.expire(key, CACHE_TTL);
    }

    public void increment(Long pollId, Integer presentationOrder, Integer delta) {
        redisTemplate.opsForHash().increment(key(pollId), Integer.toString(presentationOrder), delta);
        redisTemplate.expire(key(pollId), CACHE_TTL);
    }

    public void invalidate(Long pollId) {
        redisTemplate.delete(key(pollId));
    }

}
