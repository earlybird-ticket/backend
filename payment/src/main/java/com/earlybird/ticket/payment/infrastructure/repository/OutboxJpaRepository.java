package com.earlybird.ticket.payment.infrastructure.repository;

import com.earlybird.ticket.payment.domain.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxJpaRepository extends JpaRepository<Outbox, UUID> {

    @Query("select o from Outbox o where o.retryCount < 3 order by o.createdAt limit 100")
    List<Outbox> findTop100UnmarkedOutboxOrderByCreatedAt();
}
