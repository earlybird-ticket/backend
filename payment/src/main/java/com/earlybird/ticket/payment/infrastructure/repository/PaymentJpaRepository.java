package com.earlybird.ticket.payment.infrastructure.repository;

import com.earlybird.ticket.payment.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {

}
