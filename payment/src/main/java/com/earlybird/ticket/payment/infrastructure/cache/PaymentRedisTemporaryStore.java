package com.earlybird.ticket.payment.infrastructure.cache;

import com.earlybird.ticket.payment.application.TemporaryStore;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRedisTemporaryStore implements TemporaryStore {

    private static final String RESERVATION_KEY = "TIME_LIMIT:RESERVATION_ID:";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean isTimedOut(UUID reservationId) {
        String key = RESERVATION_KEY + reservationId;
        log.info("TTL 확인 = {}", key);
        Long expire = stringRedisTemplate.getExpire(key);
        log.info("남은 시간 = {}", expire);
        return expire <= 0L;
    }

}
