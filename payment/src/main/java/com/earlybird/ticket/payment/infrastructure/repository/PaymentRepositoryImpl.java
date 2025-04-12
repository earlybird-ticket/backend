package com.earlybird.ticket.payment.infrastructure.repository;

import com.earlybird.ticket.payment.domain.entity.Payment;
import com.earlybird.ticket.payment.domain.repository.PaymentRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findByPaymentId(UUID paymentId) {
        return paymentJpaRepository.findById(paymentId);
    }
}
