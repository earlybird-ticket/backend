package com.earlybird.ticket.payment.infrastructure.cache;

import com.earlybird.ticket.payment.application.TemporaryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRedisTemporaryStore implements TemporaryStore {

    private static final String RESERVATION_KEY = "TIME_LIMIT:RESERVATION_ID:";

    private final RedissonClient redissonClient;

    /**
     * 예약 쪽 TTL을 확인 -> 현재 버킷이 없다면 이미 TTL 만료
     *
     * @param reservationId
     * @return
     */
    @Override
    public boolean isTimedOut(UUID reservationId) {
        String key = RESERVATION_KEY + reservationId;
        log.info("[Time = {} ] 예약 생성 시간 검증 = {}",
                 LocalDateTime.now(),
                 key);
        return !redissonClient.getBucket(key)
                              .isExists();
    }

}
