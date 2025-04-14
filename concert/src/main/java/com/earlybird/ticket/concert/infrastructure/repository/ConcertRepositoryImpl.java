package com.earlybird.ticket.concert.infrastructure.repository;

import com.earlybird.ticket.concert.domain.Concert;
import com.earlybird.ticket.concert.domain.ConcertRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertQueryDslRepository concertQueryDslRepository;

    @Transactional
    @Override
    public void save(Concert concert) {
        concertJpaRepository.save(concert);
    }

    @Override
    public Optional<Concert> findByConcertId(UUID uuid) {
        return concertJpaRepository.findById(uuid);
    }
}
