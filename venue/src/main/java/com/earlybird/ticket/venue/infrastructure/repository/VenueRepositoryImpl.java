package com.earlybird.ticket.venue.infrastructure.repository;

import com.earlybird.ticket.venue.domain.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VenueRepositoryImpl implements VenueRepository {
    private final VenueJpaRepository venueJpaRepository;
}
