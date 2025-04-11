package com.earlybird.ticket.venue.domain.repository;

import com.earlybird.ticket.venue.domain.entity.Outbox;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository {
    Outbox save(Outbox outbox);
}
