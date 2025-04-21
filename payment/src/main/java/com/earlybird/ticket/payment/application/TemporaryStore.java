package com.earlybird.ticket.payment.application;

import java.util.UUID;

public interface TemporaryStore {

    boolean isTimedOut(UUID reservationId);

    boolean isAlreadyProcessed(UUID reservationId);

    void cacheConfirmedPayment(UUID reservationId);
}
