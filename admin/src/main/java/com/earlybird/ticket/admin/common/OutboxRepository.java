package com.earlybird.ticket.admin.common;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    List<Outbox> findAllByCreatedAtLessThanEqualAndSuccessFalseOrderByCreatedAtAsc(
            LocalDateTime createdAt, Pageable pageable);
}
