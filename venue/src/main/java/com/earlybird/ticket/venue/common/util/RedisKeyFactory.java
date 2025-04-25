package com.earlybird.ticket.venue.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisKeyFactory {

    public static final String RESERVATION_TIME_LIMIT_PREFIX = "TIME_LIMIT:RESERVATION_ID:";
    public static final String SEAT_INSTANCE_PREFIX = "SEAT_INSTANCE:";
    public static final String SECTION_LIST_PREFIX = "SECTION_LIST:";
    public static final String SEAT_INDEX_PREFIX = "SEAT_INDEX:";
    public static final String PREFIX_WILDCARD = "*:";
    public static final String SUFFIX_WILDCARD = ":*";
    public static final String REDIS_COLON = ":";

    private final RedisKeyScanner redisKeyScanner;
    private final StringRedisTemplate stringRedisTemplate;

    public String generateSeatIndexKey(UUID concertSequenceId, String section) {
        return SEAT_INDEX_PREFIX + concertSequenceId + REDIS_COLON + section;
    }

    public String generateSectionListKey(UUID concertId, UUID concertSequenceId, String section) {
        return SECTION_LIST_PREFIX + concertId + REDIS_COLON + concertSequenceId + REDIS_COLON + section;
    }

    public String generateSeatInstanceKey(UUID concertSequenceId, UUID seatInstanceId) {
        return SEAT_INSTANCE_PREFIX + concertSequenceId + REDIS_COLON + seatInstanceId;
    }

    public String generateSeatInstanceKeyWithOutSeatInstanceId(UUID concertSequenceId) {
        return SEAT_INSTANCE_PREFIX + concertSequenceId + REDIS_COLON;
    }

    public List<String> getAllSectionListKeys(UUID concertSequenceId, Long count) {
        return redisKeyScanner.scanKeys(
                SECTION_LIST_PREFIX +
                        PREFIX_WILDCARD +
                        concertSequenceId +
                        SUFFIX_WILDCARD,
                count
        );
    }

    public List<String> convertSeatIndexKeysToSeatInstanceKeys(UUID concertSequenceId, String section, Long startIndex, Long endIndex) {
        return Optional.ofNullable(stringRedisTemplate.opsForZSet().range(
                generateSeatIndexKey(concertSequenceId, section),
                                startIndex,
                                endIndex)
                )
                .orElse(Collections.emptySet())
                .stream()
                .map(id -> SEAT_INSTANCE_PREFIX + concertSequenceId + REDIS_COLON + id)
                .toList();
    }
}
