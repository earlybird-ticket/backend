package com.earlybird.ticket.alarm.domain.repository;


import com.earlybird.ticket.alarm.domain.Outbox;

import java.util.List;

public interface OutboxRepository {
    Outbox save(Outbox outbox);

    List<Outbox> findTOP100ByOrderByCreatedAtAsc();

    void saveAll(List<Outbox> updatedOutboxes);
}
