package com.earlybird.ticket.venue.infrastructure.repository;

import com.earlybird.ticket.common.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

}
