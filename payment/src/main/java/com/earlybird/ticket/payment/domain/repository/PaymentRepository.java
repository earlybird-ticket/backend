package com.earlybird.ticket.payment.domain.repository;

import com.earlybird.ticket.payment.domain.entity.Payment;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Optional<Payment> findByPaymentId(UUID paymentId);

    Payment save(Payment payment);
}
