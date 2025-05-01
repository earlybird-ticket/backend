package com.earlybird.ticket.payment.infrastructure.repository;

import com.earlybird.ticket.payment.domain.entity.Outbox;
import com.earlybird.ticket.payment.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public Outbox save(Outbox outbox) {
        return outboxJpaRepository.save(outbox);
    }

    @Override
    public List<Outbox> saveAll(List<Outbox> outboxes) {
        return outboxJpaRepository.saveAll(outboxes);
    }

    @Override
    public List<Outbox> findTop100UnmarkedOutboxOrderByCreatedAt() {
        return outboxJpaRepository.findTop100UnmarkedOutboxOrderByCreatedAt();
    }

}
