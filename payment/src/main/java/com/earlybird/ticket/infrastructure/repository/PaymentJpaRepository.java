package com.earlybird.ticket.infrastructure.repository;

import com.earlybird.ticket.domain.entity.Payment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, UUID> {

}
