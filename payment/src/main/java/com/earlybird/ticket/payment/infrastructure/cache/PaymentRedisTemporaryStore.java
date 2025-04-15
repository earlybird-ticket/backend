package com.earlybird.ticket.payment.infrastructure.cache;

import com.earlybird.ticket.payment.application.TemporaryStore;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRedisTemporaryStore implements TemporaryStore {

    private static final String RESERVATION_KEY = "TIME_LIMIT:RESERVATION_ID:";

    private final RedissonClient redissonClient;

    @Override
    public boolean isTimedOut(UUID reservationId) {
        String key = RESERVATION_KEY + reservationId;
        log.info("예약 생성 시간 검증 = {}", key);
        return redissonClient.getBucket(key).isExists();
    }

}
