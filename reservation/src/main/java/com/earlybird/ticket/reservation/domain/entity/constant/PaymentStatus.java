package com.earlybird.ticket.reservation.domain.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    CREATED(Status.CREATED),
    WAIT_FOR_DEPOSIT(Status.WAITING_FOR_DEPOSIT),
    DONE(Status.DONE),
    ABORTED(Status.ABORTED),
    EXPIRED(Status.EXPIRED);

    private final String value;

    public static PaymentStatus from(String status) {
        return Arrays.stream(values())
                     .filter(paymentStatus -> paymentStatus.value.equals(status))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Invalid payment status: " + status));
    }

    static class Status {

        public static final String CREATED = "CREATED";
        public static final String DONE = "DONE";
        // 가상계좌만
        public static final String WAITING_FOR_DEPOSIT = "WAITING_FOR_DEPOSIT";
        // 10분 안에는 재시도 언제든지 가능
        public static final String ABORTED = "ABORTED";
        // 10분 이후에는 좌석 반환과 함께 결제 실패
        public static final String EXPIRED = "EXPIRED";
    }
}
