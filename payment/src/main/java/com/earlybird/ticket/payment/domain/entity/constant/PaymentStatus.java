package com.earlybird.ticket.payment.domain.entity.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    CREATED(Status.CREATED),
    PENDING(Status.PENDING),
    PAID(Status.PAID),
    FAILED(Status.FAILED);

    private final String value;

    static class Status {

        public static final String CREATED = "CREATED";
        public static final String PENDING = "PENDING";
        public static final String PAID = "PAID";
        public static final String FAILED = "FAILED";

    }
}
