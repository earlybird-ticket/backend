package com.earlybird.ticket.concert.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ConcertRepository {

    void save(Concert concert);

    Optional<Concert> findByConcertId(UUID uuid);

    Page<Concert> searchConcert(String q, String sort, String orderBy, PageRequest pageRequest);
}
