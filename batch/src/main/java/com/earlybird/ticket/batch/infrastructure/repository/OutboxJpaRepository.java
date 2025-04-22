package com.earlybird.ticket.batch.infrastructure.repository;

import com.earlybird.ticket.batch.domain.entity.Outbox;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxJpaRepository extends JpaRepository<Outbox, UUID> {

}
