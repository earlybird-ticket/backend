package com.earlybird.ticket.venue.infrastructure.repository;

import com.earlybird.ticket.venue.domain.entity.Outbox;
import com.earlybird.ticket.venue.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {
    private final OutboxJpaRepository outboxJpaRepository;


    @Override
    public Outbox save(Outbox outbox) {
        return outboxJpaRepository.save(outbox);
    }
}
