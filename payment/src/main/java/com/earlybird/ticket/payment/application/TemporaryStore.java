package com.earlybird.ticket.payment.application;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TemporaryStore {

    boolean isTimedOut(UUID reservationId);

    boolean isAlreadyProcessed(UUID reservationId);

    void cacheConfirmedPayment(UUID reservationId);

    LocalDateTime getExpireDate(UUID reservationId);

    Long getRemainingTime(UUID reservationId);
}
