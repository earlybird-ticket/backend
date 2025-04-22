package com.earlybird.ticket.alarm.infrastructure.repository.outbox;

import com.earlybird.ticket.alarm.domain.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<Outbox, Long> {

    List<Outbox> findTOP100ByOrderByCreatedAtAsc();
}
