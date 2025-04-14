package com.earlybird.ticket.concert.domain;

import java.util.Optional;
import java.util.UUID;

public interface ConcertRepository {

    void save(Concert concert);

    Optional<Concert> findByConcertId(UUID uuid);
}
