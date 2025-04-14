package com.earlybird.ticket.payment.domain.repository;


import com.earlybird.ticket.payment.domain.entity.Outbox;
import java.util.List;

public interface OutboxRepository {

    Outbox save(Outbox outbox);

    List<Outbox> findTop100UnmarkedOutboxOrderByCreatedAt();
}
