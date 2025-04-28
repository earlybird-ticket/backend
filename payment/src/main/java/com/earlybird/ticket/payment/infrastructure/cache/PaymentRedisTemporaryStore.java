package com.earlybird.ticket.payment.infrastructure.cache;

import com.earlybird.ticket.payment.application.TemporaryStore;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRedisTemporaryStore implements TemporaryStore {

    private static final String RESERVATION_KEY = "TIME_LIMIT:RESERVATION_ID:";
    private static final String PAYMENT_CONFIRM_KEY = "PAYMENT:CONFIRM:";

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
        log.info("예약 생성 시간 검증 = {}", key);
        return !redissonClient.getBucket(key).isExists();
    }

    @Override
    public boolean isAlreadyProcessed(UUID reservationId) {
        String key = PAYMENT_CONFIRM_KEY + reservationId;
        return redissonClient.getBucket(key).isExists();
    }

    @Override
    public void cacheConfirmedPayment(UUID reservationId) {
        String key = PAYMENT_CONFIRM_KEY + reservationId;
        log.info("결제 캐싱 : {}", reservationId);
        RBucket<Object> paymentConfirm = redissonClient.getBucket(key);

        if (!paymentConfirm.isExists()) {
            paymentConfirm.setIfAbsent(key, Duration.ofMinutes(10L));
        }
    }

    @Deprecated
    @Override
    public LocalDateTime getExpireDate(UUID reservationId) {
        String key = RESERVATION_KEY + reservationId;

        RBucket<Object> bucket = redissonClient.getBucket(key);

        return Instant
            .now()
            .atZone(ZoneId.systemDefault())
            .plusSeconds(bucket.getExpireTime())
            .toLocalDateTime();
    }

    @Override
    public Long getRemainingTime(UUID reservationId) {
        String key = RESERVATION_KEY + reservationId;

        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.getExpireTime();
    }
}
