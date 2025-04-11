package com.earlybird.ticket.reservation.infrastructure.repository.outbox;

import com.earlybird.ticket.reservation.domain.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {

    List<Outbox> findTOP100ByOrderByCreatedAtAsc();
}
