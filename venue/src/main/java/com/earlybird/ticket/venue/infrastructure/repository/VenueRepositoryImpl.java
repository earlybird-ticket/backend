package com.earlybird.ticket.venue.infrastructure.repository;

import com.earlybird.ticket.venue.domain.entity.Venue;
import com.earlybird.ticket.venue.domain.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VenueRepositoryImpl implements VenueRepository {
    private final VenueJpaRepository venueJpaRepository;
    private final VenueQueryRepository venueQueryRepository;

    @Override
    public Venue save(Venue venue) {
        return venueJpaRepository.save(venue);
    }

    @Override
    public Venue findById(UUID venueId) {
        return venueJpaRepository.findById(venueId).orElse(null);
    }

    @Override
    public Venue findVenueWithHallById(UUID venueId) {
        return venueQueryRepository.findVenueWithHallById(venueId);
    }
}
