package com.earlybird.ticket.payment.domain.repository;


import com.earlybird.ticket.payment.domain.entity.Outbox;
import java.util.List;

public interface OutboxRepository {

    Outbox save(Outbox outbox);

    List<Outbox> saveAll(List<Outbox> outboxes);

    List<Outbox> findTop100UnmarkedOutboxOrderByCreatedAt();
}
