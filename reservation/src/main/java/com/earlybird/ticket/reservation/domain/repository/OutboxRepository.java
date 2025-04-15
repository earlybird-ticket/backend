package com.earlybird.ticket.reservation.domain.repository;

import com.earlybird.ticket.reservation.domain.entity.Outbox;

import java.util.List;

public interface OutboxRepository {
    Outbox save(Outbox outbox);

    List<Outbox> findTOP100ByOrderByCreatedAtAsc();

    void saveAll(List<Outbox> updatedOutboxes);
}
