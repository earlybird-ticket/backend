package com.earlybird.ticket.reservation.infrastructure.repository.outbox;

import com.earlybird.ticket.reservation.domain.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {

}
